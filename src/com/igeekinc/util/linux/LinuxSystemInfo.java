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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Date;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.igeekinc.util.ClientFile;
import com.igeekinc.util.ClientFileMetaData;
import com.igeekinc.util.EthernetID;
import com.igeekinc.util.FileCopy;
import com.igeekinc.util.FilePackage;
import com.igeekinc.util.FilePath;
import com.igeekinc.util.Group;
import com.igeekinc.util.GroupIterator;
import com.igeekinc.util.OSType;
import com.igeekinc.util.PowerManager;
import com.igeekinc.util.SecurityManager;
import com.igeekinc.util.SystemInfo;
import com.igeekinc.util.User;
import com.igeekinc.util.UserIterator;
import com.igeekinc.util.Volume;
import com.igeekinc.util.VolumeManager;
import com.igeekinc.util.exceptions.FeatureNotSupportedException;
import com.igeekinc.util.exceptions.GroupNotFoundException;
import com.igeekinc.util.exceptions.UserNotFoundException;
import com.igeekinc.util.fileinfo.FileInfoDBManager;
import com.igeekinc.util.fsevents.FSEventsProcessor;
import com.igeekinc.util.linux.nativeifs.LibC;
import com.igeekinc.util.logging.ErrorLogMessage;
import com.igeekinc.util.msgpack.ClientFileMetaDataMsgPack;
import com.igeekinc.util.scripting.ScriptExecutor;
import com.igeekinc.util.xmlserial.XMLObjectParseHandler;
import com.igeekinc.util.xmlserial.XMLObjectSerializeHandler;
public class LinuxSystemInfo extends SystemInfo
{
	private static LinuxVolumeManager volumeManager;
	private static LinuxPowerManager powerManager;
	public static void loadNative()
	{
		if (System.getProperty("com.igeekinc.util.libindelible") != null)
		{
			File library = new File(System.getProperty("com.igeekinc.util.libindelible"));
			System.load(library.getAbsolutePath());
		}
		else
		{
			try
			{
				System.loadLibrary("Indelible");
			}
			catch (UnsatisfiedLinkError e)
			{
				Logger.getLogger(LinuxSystemInfo.class).error(new ErrorLogMessage("Could not load libIndelible"), e);
			}
		}
	}
	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getPowerManager()
	 */
	public PowerManager getPowerManager()
	{
		if (powerManager == null)
		{
			powerManager = new LinuxPowerManager();
		}
		return powerManager;
	}

  public LinuxSystemInfo()
  {
  }

  public EthernetID getEthernetID()
  {
    Runtime         curRuntime = Runtime.getRuntime();
    Process         queryProc;
    InputStream     queryStream;
    BufferedReader  queryReader;
    EthernetID      returnID=null;
    try
    {
      queryProc = curRuntime.exec("/sbin/ifconfig");

      queryStream = queryProc.getInputStream();
      queryReader = new BufferedReader(new InputStreamReader(queryStream));

      String  curLine;
      while ((curLine = queryReader.readLine()) != null)
      {
        curLine = curLine.trim();
        if (curLine.indexOf("HWaddr") >= 0)
        {
          String  addr;

          addr = curLine.substring(curLine.indexOf("HWaddr")+7).trim();

          returnID = new EthernetID(addr);
          return(returnID);  // We'll just take the first one we find
        }
        if (curLine.indexOf("ether") >= 0)
        {
            String  addr;

            addr = curLine.substring(curLine.indexOf("ether")+6).trim();
            addr = addr.substring(0, addr.indexOf(" "));
            returnID = new EthernetID(addr);
            return(returnID);  // We'll just take the first one we find
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return(null);
  }

  public String getSystemName()
  {
    return ("builder");
  }

  public LinuxUser getUser() throws UserNotFoundException
	{
		String userName = System.getProperty("user.name");
		return (getUserInfoForName(userName));
	}

  public LinuxUser getUserInfoForName(String userName)
  throws UserNotFoundException
  {
	    PasswordStructure passwordEntry = LibC.INSTANCE.getpwnam(userName);
	    //nativeFileRoutines.getpwnam(userName, fillIn);
	    if (passwordEntry == null)
	    {
	      throw new UserNotFoundException(userName);
	    }
	    return new LinuxUser(passwordEntry);
  }
  
  /**
   * @param ownerID
   * @return
   */
  public User getUserInfoForUID(int uid)
  throws UserNotFoundException
  {
	    PasswordStructure passwordEntry = LibC.INSTANCE.getpwuid(uid);
	    //nativeFileRoutines.getpwnam(userName, fillIn);
	    if (passwordEntry == null)
	    {
	      throw new UserNotFoundException(Integer.toString(uid));
	    }
	    return new LinuxUser(passwordEntry);
  }

  public File getGlobalPreferencesDirectory()
  {
    return new File("/etc/indelible");
  }

  public File getUserPreferencesDirectory()
  {
		LinuxUser user;
		try
		{
			user = getUser();
		} catch (UserNotFoundException e)
		{
			Logger.getLogger(getClass()).error(new ErrorLogMessage("Caught exception"), e);
			throw new InternalError("Cannot get user entry for current user");
		}
		return new File(user.getHomeDirectory(), ".indelible");
  }
  public File getTemporaryDirectory()
  {
    return new File("/tmp");
  }

  public File getLogDirectory()
  {
    return new File("/var/log/indelible");
  }
  
  public ImageIcon getFolderIcon()
  {
    return null;
  }

  public ImageIcon getVolumeIcon()
  {
    return null;
  }
	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getFileCopy()
	 */
	public FileCopy getFileCopy()
	{
		return new LinuxFileCopy();
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getBootDrive()
	 */
	public Volume getBootDrive()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getExternalDrives()
	 */
	public Volume[] getExternalDrives()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getRemovableDrives()
	 */
	public Volume[] getRemovableDrives()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#openUrl(java.net.URL)
	 */
	public boolean openUrl(URL url)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#openInTextEditor(java.io.File)
	 */
	public void openInTextEditor(File file)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getVolumeManager()
	 */
	public VolumeManager getVolumeManager()
	{
		if (volumeManager == null)
			volumeManager = new LinuxVolumeManager(null);
		return volumeManager;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getAdminUser()
	 */
	public User getAdminUser()
	{
		try
		{
			return getUserInfoForUID(0);
		}
		catch (UserNotFoundException e)
		{
			//TODO Auto-generated catch block
			org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(this.getClass());
			exceptionLogger.error("Caught exception UserNotFoundException", e);
			throw new InternalError("Couldn't find root user");
		}
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.SystemInfo#getSecurityManager()
	 */
	public SecurityManager getSecurityManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

    @SuppressWarnings("unchecked")
    public Constructor<LinuxFilePath> getFilePathConstructor()
    {
    	Class<?> [] args = {String.class, Boolean.TYPE};
    	Constructor<LinuxFilePath> returnConstructor = null;
    	try
    	{
    		returnConstructor = (Constructor<LinuxFilePath>) Class.forName("com.igeekinc.util.linux.LinuxFilePath").getConstructor(args);	 //$NON-NLS-1$
    	}
    	catch (SecurityException e)
    	{
    		//TODO Auto-generated catch block
    		org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(FilePath.class);
    		exceptionLogger.error("Caught exception SecurityException", e); //$NON-NLS-1$
    	}
    	catch (NoSuchMethodException e)
    	{
    		//TODO Auto-generated catch block
    		org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(FilePath.class);
    		exceptionLogger.error("Caught exception NoSuchMethodException", e); //$NON-NLS-1$
    	}
    	catch (ClassNotFoundException e)
    	{
    		//TODO Auto-generated catch block
    		org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(FilePath.class);
    		exceptionLogger.error("Caught exception ClassNotFoundException", e); //$NON-NLS-1$
    	}
    	return returnConstructor;
    }
    public User getExecutingUser() throws UserNotFoundException
    {
    	return getUserInfoForUID(LibC.INSTANCE.getuid());
    }



	@Override
	public void displayPDFFile(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User[] getActiveGUIUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getCacheDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public File getRunDirectory()
	{
		return new File("/var/run");
	}
	
	@Override
	public FSEventsProcessor getFSEventsProcessor()
			throws FeatureNotSupportedException {
		throw new FeatureNotSupportedException();
	}

	@SuppressWarnings("unchecked")
    @Override
	public Class<? extends FilePath> getFilePathClass() 
	{
	    Class<? extends FilePath> returnClass = null;
        try
        {
            returnClass = (Class<? extends FilePath>) Class.forName("com.igeekinc.util.linux.LinuxFilePath");     //$NON-NLS-1$
        }
        catch (SecurityException e)
        {
            //TODO Auto-generated catch block
            org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(FilePath.class);
            exceptionLogger.error("Caught exception SecurityException", e); //$NON-NLS-1$
        }
        catch (ClassNotFoundException e)
        {
            //TODO Auto-generated catch block
            org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(FilePath.class);
            exceptionLogger.error("Caught exception ClassNotFoundException", e); //$NON-NLS-1$
        }
        return returnClass; 
	}

	@Override
	public Group getGroupInfoForGID(int gid) throws GroupNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group getGroupInfoForName(String groupName)
			throws GroupNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupIterator getGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OSType getOSType() 
	{
		return OSType.kLinux;
	}

	@Override
	public ScriptExecutor getScriptExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getSystemBootTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIterator getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientFile getUsersDirectory() throws IOException 
	{
		LinuxUser user;
		try
		{
			user = getUser();
		} catch (UserNotFoundException e)
		{
			Logger.getLogger(getClass()).error(new ErrorLogMessage("Caught exception"), e);
			throw new InternalError("Cannot get user entry for current user");
		}
		return getClientFileForPath(user.getHomeDirectory().getAbsolutePath());
	}
    @Override
    protected FileInfoDBManager createFileInfoDBManager()
    {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int getNativeIntSize()
    {
        return 4;   // Always 4 for gcc
    }
    @Override
    public int getNativeLongSize()
    {
        if (is64BitVM())
            return 8;
        else
            return 4;
    }
	@Override
	public File getPreferencesDirectoryForUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public File getUserLogDirectory() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void restartSystem() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public long getSystemIdleTime()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Class<? extends FilePackage> getFilePackageClass()
	{
		return LinuxFilePackage.class;
	}
	
	@Override
	public Class<?> getMetaDataClass()
	{
		return LinuxFileMetaData.class;
	}
	@Override
	public Class<? extends XMLObjectSerializeHandler<? extends ClientFileMetaData>> getMetaDataSerializerClass()
	{
		return LinuxFileMetaDataSerializeHandler.class;
	}
	@Override
	public Class<? extends XMLObjectParseHandler<? extends ClientFileMetaData>> getMetaDataParserClass()
	{
		return LinuxFileMetaDataParseHandler.class;
	}
	@Override
	public Class<? extends ClientFileMetaDataMsgPack> getMetaDataMsgPackSerializer()
	{
		return LinuxFileMetaDataMsgPack.class;
	}
}