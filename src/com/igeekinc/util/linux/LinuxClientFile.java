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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.igeekinc.util.ClientFile;
import com.igeekinc.util.ClientFileMetaData;
import com.igeekinc.util.FileLikeFilenameFilter;
import com.igeekinc.util.Volume;
import com.igeekinc.util.exceptions.ForkNotFoundException;
import com.igeekinc.util.linux.nativeifs.LibC;
import com.igeekinc.util.linux.nativeifs.LinuxNativeFileRoutines;
import com.igeekinc.util.linux.unixerrors.LinuxErrorException;
import com.igeekinc.util.unix.UnixDate;

public class LinuxClientFile
	extends ClientFile
	implements Serializable
{
	static final long serialVersionUID=3425451133824406774L;
	
	protected LinuxFileMetaData md = null;
	static Logger logger = Logger.getLogger(LinuxClientFile.class);
	static LinuxNativeFileRoutines nativeFileUtil =
		new LinuxNativeFileRoutines();
	public LinuxClientFile(String base, String fileName) throws IOException
	{
		super(base, fileName);
		init(true);
	}
	public LinuxClientFile(LinuxClientFile base, String fileName) throws IOException
	{
		super(base, fileName);
		init(true);
	}
	public LinuxClientFile(Volume volume, String base, String fileName, boolean inIsMountPoint) throws IOException
	{
		super(volume, base, fileName);
		init(false);
		isMountPoint = inIsMountPoint;

	}
	public LinuxClientFile(
		Volume volume,
		LinuxClientFile base,
		String fileName,
		boolean inIsMountPoint) throws IOException
	{
		super(volume, base, fileName);
		init(false);
		isMountPoint = inIsMountPoint;
	}
	
	void init(boolean checkMountPoint) throws IOException
	{
		if (checkMountPoint)
			isMountPoint = checkMountPoint();
	}
	
	public void setMetaData(ClientFileMetaData inMD) throws IOException
	{
			LinuxFileMetaData newMetaData = (LinuxFileMetaData) inMD;
			getMetaData(false);	
			if (md.getFileType() == LinuxFileMetaData.kSymbolicLinkType)
				return;	// Can't set attributes on a symbolic link
			md.setFromMetaData(newMetaData);
		

		String filePath = getAbsolutePath();

		nativeFileUtil.chmod(filePath, md.getAccessMask());
		if (LibC.INSTANCE.geteuid() == 0)
		{
		    logger.debug("Setting owner of "+filePath+" to user = "+md.getOwnerID()+", group ="+md.getGroupID());
		    int retVal;
		    if ((retVal = nativeFileUtil.chown(filePath, md.getOwnerID(), md.getGroupID())) != 0)
		        throw new IOException("Could not set owner "+md.getOwnerID()+", group "+md.getGroupID()+" on file '"+filePath+"' error ="+retVal);
		}
		
		UnixDate [] newDates;
		if ((newDates = md.getUpdatedTimes(false)) != null)
		{
			int result = nativeFileUtil.utimes(filePath, newDates[0].getSecs(),
					newDates[0].getUSecs(), newDates[1].getSecs(), newDates[1].getUSecs());
			if (result != 0)
			  throw new IOException("Could not set file "+getAbsolutePath()+" times error = "+result);
		}

		/*
		byte [] commonData = new byte[MacOSXFileMetaData.kSetCommonLen];
		int commonFlags = md.getCommonData(commonData);
		if (commonFlags != 0)
		{
		  int result = nativeFileUtil.setattrlist(this.getAbsolutePath(), commonData,
			  commonFlags, 0, 0, 0, 0, 0);
		  if (result != 0)
			throw new IOException("Could not set file metadata error = "+result);
		}
		*/
	}
	public ClientFileMetaData getMetaData()
	throws IOException
	{
		return(getMetaData(false));
	}
	
	private ClientFileMetaData getMetaData(boolean forceHFSMetaData)
	throws IOException
	{
	    if (md == null)
	    {
	    	byte [] statBuf = new byte[StatStructure.getBufferSize()];
	        
	        md = new LinuxFileMetaData();
	        if (nativeFileUtil.lstat(getAbsolutePath(), statBuf) == 0)
	        {
	        	StatStructure statStruct = StatStructure.getStatStructure(statBuf);
	            md.setFromStatStructure(statStruct);
	            if (md.isSymlink())
	            {
	            	String symLinkTarget = nativeFileUtil.readlink(getAbsolutePath());
	            	md.setSymlinkTarget(symLinkTarget);
	            }
	            else
	            	md.setSymlinkTarget("");
	        }
	    }
		return (ClientFileMetaData)md.clone();
	}

    public boolean isDirectory() 
    {
      	if (isMountPoint)
      		return true;
        try
    	{
    		getMetaData();
    	}
    	catch (IOException e)
    	{
    		return false;
    	}
        if (exists() && md != null && md.getFileType() == LinuxFileMetaData.kDirectoryType)
          return true;
        else
          return false;
      }
    
	public void mkSymlink(String linkToPathName) throws IOException
	{
		if (linkToPathName == null)
			throw new IOException("Could not symlink "+getAbsolutePath()+" to a null path");
		int result = nativeFileUtil.symlink(linkToPathName, getAbsolutePath());
		if (result != 0)
		{
			throw new IOException("errno = " + result);
		}
	}
	
	public void mkLink(String linkToPathName) throws IOException
	{
		if (linkToPathName == null)
			throw new IOException("Could not link "+getAbsolutePath()+" to a null path");
		int result = nativeFileUtil.link(linkToPathName, getAbsolutePath());
		if (result != 0)
		{
			throw new IOException("errno = " + result);
		}
	}

	public int getNumForks() 
	{
		return 1;
	}
	
    public String[] getForkNames()
    {
        String [] returnNames={"data"};
        return returnNames;
    }
    
    @Override
    public InputStream getForkInputStream(String forkName)
		throws ForkNotFoundException
	{
		if (forkName.equals("data"))
		{
			try
			{
				return new FileInputStream(this);
			}
			catch (FileNotFoundException e)
			{
				throw new ForkNotFoundException(forkName);
			}
		}

		throw new ForkNotFoundException(forkName);
	}

    @Override
    public OutputStream getForkOutputStream(String forkName)
		throws ForkNotFoundException
	{
		if (forkName.equals("data"))
		{
			try
			{
				return new FileOutputStream(this);
			}
			catch (FileNotFoundException e)
			{
				throw new ForkNotFoundException(forkName);
			}
		}
		throw new ForkNotFoundException(forkName);
	}
	
	public boolean renameTo(File destFile)
	{
		String srcPath = getAbsolutePath();
		String destPath = destFile.getAbsolutePath();
		int result = nativeFileUtil.rename(srcPath, destPath);
		if (result != 0)
			return false;
		return true;
	}
	
	protected boolean isMountPoint = false;
	public boolean isMountPoint()
	{
	    return isMountPoint;
	}
	
	protected boolean checkMountPoint() throws IOException
	{
		byte [] statBuf = new byte[StatStructure.getBufferSize()];
	  	boolean isMountPoint = false;
	  	
	  	int result = nativeFileUtil.lstat(getAbsolutePath(), statBuf);
	  	if (result != 0)
	  	{
	  		switch (result)
	  		{
	  			case LinuxErrorException.ENOENT:
	  				break; // Do nothing for file not found
	  			default:
	  				throw LinuxErrorException.getErrorForNumber(result);
	  		}
	  	}
	  	else
	  	{
		  	StatStructure stats = StatStructure.getStatStructure(statBuf);
	  		long volumeDevID = ((LinuxVolume)this.getVolume()).volumeDevID;
	  		long fileVolID = stats.get_st_dev();
	  		if (fileVolID != volumeDevID)
	  		{
	  			String parentPath = getParent();
	  			statBuf = new byte[StatStructure.getBufferSize()];
	  			result = nativeFileUtil.lstat(parentPath, statBuf);
	  			stats = StatStructure.getStatStructure(statBuf);
	  			if (result == 0 && stats.get_st_dev() == volumeDevID)
	  				isMountPoint = true;
	  			else
	  				throw new InternalError("Opening "+getAbsolutePath()+" not on requested volume "+getAbsolutePath());
	  		}
	  	}
	  	return(isMountPoint);
	  }
	public long totalLength()
	{
		try
		{
			getMetaData();
		}
		catch (IOException e)
		{
			return(0L);
		}
		if (md != null)
			return (md.getFileLength());
		else
			return (0L);
	}
	@Override
	public FileChannel getForkChannel(String forkName, boolean writeable) throws ForkNotFoundException 
	{
		return getForkChannel(forkName, false, writeable);
	}
	
	@Override
	public LinuxClientFile[] listClientFiles(FilenameFilter filter) 
	{
		return listClientFilesInt(filter, LinuxClientFile.class);
	}
	
	public String[] list(FileLikeFilenameFilter filter) throws IOException 
	{
		if (!isDirectory())
			return null;
		String names[] = super.list();
		if (names == null)
			return null;
		ArrayList<String>nameList = new ArrayList<String>();
		for (int curNameNum = 0; curNameNum < names.length; curNameNum++)
		{
			if (filter == null || filter.accept(this, names[curNameNum]))
			{
				nameList.add(names[curNameNum]);
			}
		}
		return nameList.toArray(new String[0]);
	}
	@Override
	public FileChannel getForkChannel(String forkName, boolean noCache, boolean writeable)
			throws ForkNotFoundException 
	{
		try {
			if (getMetaData().getFileType() != LinuxFileMetaData.kRegularFileType)
				throw new ForkNotFoundException("Not a regular file");
		} catch (IOException e1) {
			throw new ForkNotFoundException("IOException retrieving metadata");
		}
		if (forkName.equals("data"))
		{
			try
			{
				RandomAccessFile raf;
				// TODO - figure out how and when to close the RandomAccessFile
				if (writeable)
					raf = new RandomAccessFile(this, "rw");
				else
					raf = new RandomAccessFile(this, "r");
				return raf.getChannel();
			}
			catch (FileNotFoundException e)
			{
				throw new ForkNotFoundException(forkName);
			}
		}
		throw new ForkNotFoundException(forkName);
	}
	@Override
	public InputStream getForkInputStream(String streamName, boolean noCache)
			throws ForkNotFoundException {
		throw new UnsupportedOperationException();
	}
	@Override
	public OutputStream getForkOutputStream(String streamName,
			boolean noCache) throws ForkNotFoundException {
		throw new UnsupportedOperationException();
	}
}
