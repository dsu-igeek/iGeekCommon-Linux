/*
 * Copyright 2002-2014 iGeek, Inc.
 * All Rights Reserved
 * @Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.@
 */
package com.igeekinc.util.linux;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.igeekinc.util.CheckCorrectDispatchThread;
import com.igeekinc.util.FilePath;
import com.igeekinc.util.FilePathMap;
import com.igeekinc.util.Volume;
import com.igeekinc.util.VolumeManager;
import com.igeekinc.util.linux.nativeifs.LinuxNativeFileRoutines;
import com.igeekinc.util.linux.unixerrors.PermissionDeniedErrorException;
import com.igeekinc.util.logging.DebugLogMessage;
import com.igeekinc.util.logging.ErrorLogMessage;
import com.igeekinc.util.logging.iGeekLevel;

class VolumeCoreHolder
{
	LinuxVolumeCore core;
	LinuxVolume cachedVolume;
	
	VolumeCoreHolder(LinuxVolumeCore inCore, LinuxVolume inCachedVolume)
	{
		core = inCore;
		cachedVolume = inCachedVolume;
	}
	
    public LinuxVolumeCore getCore()
    {
        return core;
    }
    
    public LinuxVolume getCachedVolume()
    {
        return cachedVolume;
    }
    
	public boolean equals(Object checkObject)
	{
		if (checkObject instanceof VolumeCoreHolder )
			return core.equals(((VolumeCoreHolder)checkObject).core);
		return false;
	}
}

public class LinuxVolumeManager extends VolumeManager  implements Runnable
{
	LinuxNativeFileRoutines nativeRoutines = new LinuxNativeFileRoutines();
	
	Thread volumePollThread;
	boolean volumePollRunning = false;
	boolean volumesUpdated = false, diskArbFirstFinished = false, volumesPolledOnce = false, waitingForPoll = false;
	ArrayList<VolumeCoreHolder> volumeCores;
    FilePathMap<VolumeCoreHolder> volumesByPath;
    
	Logger logger;
	Level pollLevel = iGeekLevel.POLLER_DEBUG;
	
	public LinuxVolumeManager(CheckCorrectDispatchThread checker)
	{
		super(checker);
		volumesByPath = new FilePathMap<VolumeCoreHolder>();
		logger = Logger.getLogger(this.getClass());
		volumeCores = new ArrayList<VolumeCoreHolder>();
		volumePollThread = new Thread(this, "Volume PollThread");
		volumePollThread.start();
	}
  
	synchronized void waitForVolumes()
	{
		while (!volumesUpdated || !volumesPolledOnce)
		{
			try
			{
				logger.log(pollLevel, "waiting for volumes to be updated volumesUpdated = "+volumesUpdated+" volumesPolledOnce = "+volumesPolledOnce);
				wait();
				logger.log(pollLevel, "volumes should be updated now volumesUpdated = "+volumesUpdated+" volumesPolledOnce = "+volumesPolledOnce);
			}
			catch (InterruptedException e)
			{
				//TODO Auto-generated catch block
				org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(this.getClass());
				exceptionLogger.error("Caught exception InterruptedException", e);
			}
		}
	}
	public Volume [] getVolumes()
	{
	  Volume [] returnVolumes;
	  logger.log(pollLevel, "Entering getVolumes");

	  waitForVolumes();
	  synchronized(volumeCores)
	  {
		  returnVolumes = new Volume[volumeCores.size()];
		  for (int curVolumeNum = 0; curVolumeNum < volumeCores.size(); curVolumeNum++)
		  {
		  	VolumeCoreHolder curHolder = (VolumeCoreHolder)volumeCores.get(curVolumeNum);
		  	returnVolumes[curVolumeNum] = curHolder.cachedVolume;
		  }
	  }
	  return(returnVolumes);
	}
  
	public void run()
	{
		ArrayList<LinuxVolumeCore>          volumeVec = new ArrayList<LinuxVolumeCore>();
		logger.log(pollLevel, "Poll thread entered");
		
		volumesUpdated = true;	// Fudge this in case we've waited too long
		
		logger.log(pollLevel, "Entering poll loop");
		while(true)
		{	
			try
			{
				//sleepPauser.checkPauseAndAbort();	// We just eat aborted exceptions and continue
				volumeVec = listVolumes();
				logger.log(pollLevel, "Number of file systems mounted = "+volumeVec.size());

				
				ArrayList<PropertyChangeEvent> addRemoveEvents = new ArrayList<PropertyChangeEvent>();
				logger.log(pollLevel, "poll thread - Entering volumeCores synchronized block");
				synchronized (volumeCores)
				{
					Iterator<VolumeCoreHolder> oldVolumesIterator = volumeCores.iterator();
					while(oldVolumesIterator.hasNext())
					{
						VolumeCoreHolder oldHolder = oldVolumesIterator.next();
						LinuxVolumeCore oldVolumeCore = oldHolder.core;
						if (!volumeVec.contains(oldVolumeCore))
						{
							logger.debug("Removing file system "+oldVolumeCore.getMountPoint());
							LinuxVolume removeVolume = oldHolder.cachedVolume;
							if (removeVolume != null)
							{	
								PropertyChangeEvent removeEvent =
									new PropertyChangeEvent(
											this,
											"volumeRemoved",
											null,
											removeVolume);
								logger.log(pollLevel, "Queuing volume removed event for later");
								addRemoveEvents.add(removeEvent);
							}
							oldVolumesIterator.remove();
							volumesByPath.remove(FilePath.getFilePath(oldHolder.getCore().getMountPoint()));
						}
					}
					for (int newVolumeNum = 0;
							newVolumeNum < volumeVec.size();
							newVolumeNum++)
					{
						LinuxVolumeCore newVolumeCore = volumeVec.get(newVolumeNum);
						VolumeCoreHolder searchHolder = new VolumeCoreHolder(newVolumeCore, null);
						if (volumeCores.contains(searchHolder))
						{
							int oldIndex = volumeCores.indexOf(searchHolder);
							VolumeCoreHolder oldHolder = (VolumeCoreHolder)volumeCores.get(oldIndex);
							if (!oldHolder.core.getMountPoint().equals(newVolumeCore.getMountPoint()))
							{
								logger.debug("Core changed- old mntonname = "+oldHolder.core.getMountPoint()+
										", new mntonname = "+ newVolumeCore.getMountPoint()+
										" sending remove event");
								LinuxVolume removeVolume = oldHolder.cachedVolume;
								if (removeVolume != null)
								{	
									PropertyChangeEvent removeEvent =
										new PropertyChangeEvent(
												this,
												"volumeRemoved",
												null,
												removeVolume);
									logger.log(pollLevel, "Queuing volume removed event for later");
									addRemoveEvents.add(removeEvent);
								}
								volumeCores.remove(oldHolder);
								volumesByPath.remove(FilePath.getFilePath(oldHolder.getCore().getMountPoint()));
								// Add the correct volume
								try
								{
									addNewVolumeCore(newVolumeCore, addRemoveEvents);
								}
								catch (IOException e)
								{
									logger.error("Got IOException add new core "+newVolumeCore.getMountPoint(), e);
								}
							}
						}
						else
						{
							try
							{
								addNewVolumeCore(newVolumeCore, addRemoveEvents);
							}
							catch(PermissionDeniedErrorException p)
							{
								// Just silently ignore it.  There's a stupid .gvfs file system which we can't
								// access as root
							}
							catch (IOException e)
							{
								logger.error("Got IOException add new core "+newVolumeCore.getMountedDevice(), e);
							}
						}
					}
				}
				logger.log(pollLevel, "Setting volume poll running to true");
				volumePollRunning = true;
				synchronized(this)
				{
					logger.log(pollLevel, "Setting volumesPolledOnce = true and notifying");
					volumesPolledOnce = true;
					waitingForPoll = false;
					notifyAll();
				}
				for (int curEventNum = 0; curEventNum < addRemoveEvents.size(); curEventNum++)
				{
					logger.log(pollLevel, "Firing deferred event "+curEventNum);
					PropertyChangeEvent curEvent = addRemoveEvents.get(curEventNum);
					fireEventOnCorrectThread(curEvent);
				}
			}
			catch (Exception e)
			{
				logger.error("Exception thrown in volume poll thread", e);
			}
			catch (Error e)
			{
				logger.error("Error thrown in volume poll thread", e);
			}
			synchronized(this)
			{
				try
				{
					wait(10000);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
	}

	private ArrayList<LinuxVolumeCore> listVolumes() 
	{
		ArrayList<LinuxVolumeCore>returnList = new ArrayList<LinuxVolumeCore>();
		BufferedReader mountReader = null;
		try 
		{
			mountReader = new BufferedReader(new FileReader(new File("/proc/mounts")));
			String curLine;
			
			while ((curLine = mountReader.readLine()) != null)
			{
				StringTokenizer mountTokenizer = new StringTokenizer(curLine, " ");
				if (mountTokenizer.countTokens() == 6)
				{
					String dev = mountTokenizer.nextToken();
					String mountPoint = unescapeString(mountTokenizer.nextToken());
					String fsType = mountTokenizer.nextToken();
					String options = mountTokenizer.nextToken();
					String dumpFreqNo = mountTokenizer.nextToken();
					String passNo = mountTokenizer.nextToken();

					FilePath mountPath = FilePath.getFilePath(mountPoint);
					byte [] fsInfoBuf = new byte[StatFSStructure.getBufferSize()];
					byte [] statBuf = new byte[StatStructure.getBufferSize()];
					if (nativeRoutines.statfs(mountPoint, fsInfoBuf) == 0)
					{
						StatFSStructure curStatFS = StatFSStructure.getStatFSStructure(fsInfoBuf,  0);
						
						if (nativeRoutines.lstat(mountPoint, statBuf) == 0)
						{
							StatStructure mountStatStructure = StatStructure.getStatStructure(statBuf);
							String volumeName;
							if (mountPath.getNumComponents() > 0)
								volumeName = mountPath.getName();
							else
								volumeName = "/";

							returnList.add(new LinuxVolumeCore(curStatFS, mountStatStructure, volumeName, dev, mountPoint));
						}
						else
						{
							logger.error(new ErrorLogMessage("lstat failed for volume {0}", new Serializable[]{mountPoint}));
						}
					}
					else
					{
						logger.error(new ErrorLogMessage("statfs failed for volume {0}", new Serializable[]{mountPoint}));
					}
				}
				else
				{
					logger.error(new ErrorLogMessage("Expected six tokens in mount line '"+curLine+"', got "+mountTokenizer.countTokens()));
				}
			}

		} 
		catch (IOException e) 
		{
			logger.error(new ErrorLogMessage("Got IOException reading /proc/mounts"), e);
		}
		finally
		{
			if (mountReader != null)
				try
				{
					mountReader.close();
				} catch (IOException e)
				{
					Logger.getLogger(getClass()).error(new ErrorLogMessage("Caught exception"), e);
				}
		}
		return returnList;
	}

	private String unescapeString(String escapedString)
	{
		StringBuffer unescapedBuffer = new StringBuffer(escapedString.length());
		for (int curCharNum = 0; curCharNum < escapedString.length(); curCharNum++)
		{
			char curChar = escapedString.charAt(curCharNum);
			if (curChar != '\\')
			{
				unescapedBuffer.append(curChar);
			}
			else
			{
				String octalStr = escapedString.substring(curCharNum + 1, curCharNum + 4);
				int utf8Int = Integer.parseInt(octalStr, 8);
				unescapedBuffer.append((char)utf8Int);
				curCharNum += 3;
			}
		}
		
		return unescapedBuffer.toString();
	}
	LinuxVolume makeVolumeForCore(LinuxVolumeCore newCore)
	throws IOException
	{
		LinuxVolume newVolume;
		newVolume = new LinuxVolume(newCore);
		return(newVolume);
	}
	
	void addNewVolumeCore(LinuxVolumeCore newCore, ArrayList<PropertyChangeEvent> eventsVector)
	throws IOException
	{
		logger.debug("Adding file system "+newCore.getMountPoint());
		checkAndSetExternal(newCore);
		LinuxVolume newVolume = makeVolumeForCore(newCore);
		VolumeCoreHolder holder = new VolumeCoreHolder(newCore, newVolume);
		volumeCores.add(holder);
		volumesByPath.put(FilePath.getFilePath(holder.core.getMountPoint()), holder); 
		PropertyChangeEvent addEvent =
		new PropertyChangeEvent(
				this,
				"volumeAdded",
				null,
				newVolume);
		if (eventsVector != null)
		{	
			logger.debug("Queuing volume added event for later execution");
			eventsVector.add(addEvent);
		}
		else
		{
			logger.debug("Executing volume added event now");
			fireEventOnCorrectThread(addEvent);
		}
	}
	void checkAndSetExternal(LinuxVolumeCore checkVolumeCore)
	{

	}
	 public Volume getVolumeForPath(FilePath path)
	    throws IOException
	    {
	        int numRuns = 0;
	        while (numRuns < 2)
	        {   
	            waitForVolumes();
	            synchronized(volumeCores)
	            {
	            	FilePath checkPath = path;
	            	byte [] statBuf = new byte[StatStructure.getBufferSize()];
	            	while (checkPath.getNumComponents() > 0)
	            	{
	            		if (nativeRoutines.lstat(checkPath.toString(), statBuf) == 0)
	            		{
	            			StatStructure checkStructure = StatStructure.getStatStructure(statBuf);
	            			VolumeCoreHolder volumeCoreHolder = null;
	            			for (VolumeCoreHolder curVolumeCoreHolder:volumeCores)
	            			{
	            				long checkDevID = checkStructure.get_st_dev();
								long curVolumeDevID = curVolumeCoreHolder.getCore().getMountPointInfo().get_st_dev();
								if (checkDevID == curVolumeDevID)
	            				{
	            					volumeCoreHolder = curVolumeCoreHolder;
	            					break;
	            				}
	            			}


	            			if (volumeCoreHolder != null)
	            			{
	            				Volume returnVolume = volumeCoreHolder.getCachedVolume();
	            				if (returnVolume != null)
	            					return returnVolume;
	            			}
	            			else
	            			{
	            				return null;
	            			}
	            		}
	            		else
	            		{
	            			checkPath = checkPath.getParent();
	            		}
	            	}
	            }
	            if (volumePollRunning && numRuns == 0)
	            {   
	                synchronized(this)
	                {
	                    waitingForPoll = true;
	                    while(waitingForPoll)
	                    {   
	                        // All the way down here?  Let's kick the poll thread and get it to update once
	                        notifyAll();
	                        try
	                        {
	                            wait(1000);
	                        }
	                        catch (InterruptedException e)
	                        {
	                            org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(this.getClass());
	                            exceptionLogger.error("Caught exception InterruptedException", e);
	                        }
	                    }
	                }
	            }
	            numRuns++;
	        }
	        return(null);
	    }

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getBootDrive()
	 */
	public Volume getBootDrive()
	{
		Volume bootVolume;
		try
		{
			bootVolume = getVolumeForPath("/");
		}
		catch (IOException e)
		{
			return null;
		}
		return (bootVolume);
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getExternalDrives()
	 */
	public Volume[] getExternalDrives()
	{
		logger.debug("getExternalDrives - calling getVolumes()");
		Volume [] allVolumes = getVolumes();
		ArrayList<Volume> returnVec = new ArrayList<Volume>();
		for (int curVolumeNum = 0; curVolumeNum < allVolumes.length; curVolumeNum++)
		{
			if (allVolumes[curVolumeNum].isExternal())
				returnVec.add(allVolumes[curVolumeNum]);
		}
		Volume [] returnVolumes = new Volume[returnVec.size()];
		returnVec.toArray(returnVolumes);
		return(returnVolumes);
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getRemovableDrives()
	 */
	public Volume[] getRemovableDrives()
	{
		Volume [] allVolumes = getVolumes();
		ArrayList<Volume> returnVec = new ArrayList<Volume>();
		for (int curVolumeNum = 0; curVolumeNum < allVolumes.length; curVolumeNum++)
		{
			if (allVolumes[curVolumeNum].isRemovable())
				returnVec.add(allVolumes[curVolumeNum]);
		}
		Volume [] returnVolumes = new Volume[returnVec.size()];
		returnVec.toArray(returnVolumes);
		return(returnVolumes);
	}
	
	
	
	public LinuxVolumeCore getCoreForVolume(LinuxVolume findVolume)
	{
		LinuxVolumeCore foundCore = null;
		logger.log(pollLevel, "getCoreForVolume - Entering volumeCores synchronized block");
		waitForVolumes();
		synchronized(volumeCores)
		{
			Iterator<VolumeCoreHolder> coresIterator = volumeCores.iterator();
			while (coresIterator.hasNext())
			{
				VolumeCoreHolder curHolder = coresIterator.next();
				if (curHolder.cachedVolume == null)
					continue;
			}
		}
		String volumePath;
		
		if (findVolume.volumePath == null)
		{
		    org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(this.getClass());
		    exceptionLogger.debug(new DebugLogMessage("Volume "+findVolume+" does not have alias or volumePath - cannot resolve"));
		    return null;
		}
		volumePath = findVolume.volumePath;
		
		if (volumePath == null)
		{
			logger.debug("Could not resolve returned ref for "+findVolume.getVolumeName());
			return null;
		}
		synchronized(volumeCores)
		{
			Iterator<VolumeCoreHolder> coresIterator = volumeCores.iterator();
			while (coresIterator.hasNext())
			{
				VolumeCoreHolder curHolder = coresIterator.next();
				LinuxVolumeCore curCore = curHolder.core;
				if (curCore.getMountPoint().equals(volumePath))
						return curCore;

			}
		}
		if (foundCore == null)
			logger.error("Cannot find volume core for '"+findVolume.getVolumeName()+"'("+findVolume.getFsType()+","+
					findVolume.getDeviceName()+","+findVolume.getVolumeDevID()+")");
		return null;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.VolumeManager#mountVolume(com.igeekinc.util.Volume, long)
	 */
	public int mountVolume(Volume volumeToMount, long timeout, boolean userInteractionAllowed)
	{
		if (volumeToMount.isOnline())
		{
			logger.debug("Volume "+volumeToMount+" is already mounted");
			return kMountedOK;	// Already mounted
		}
		if (!(volumeToMount instanceof LinuxVolume))
			return kMountFailed;
		logger.debug("Trying to mount volume "+volumeToMount);

		logger.debug("Volume "+volumeToMount+" mounted OK");
		return kMountedOK;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.VolumeManager#ensureMountablity(com.igeekinc.util.Volume)
	 */
	public int ensureMountablity(Volume volumeToPreflight)
	{
	    return kMountable;
	}
}
