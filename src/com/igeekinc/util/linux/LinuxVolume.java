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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.igeekinc.util.ClientFile;
import com.igeekinc.util.FilePath;
import com.igeekinc.util.SystemInfo;
import com.igeekinc.util.User;
import com.igeekinc.util.Volume;
import com.igeekinc.util.VolumeBootInfo;
import com.igeekinc.util.linux.nativeifs.LinuxNativeFileRoutines;
import com.igeekinc.util.linux.unixerrors.LinuxErrorException;

public class LinuxVolume extends Volume
{
	static final long serialVersionUID=-6939329541408431577L;
	
	protected String volumeName;
	protected String volumePath;
	protected String deviceName;
	protected String fsType;
	transient long volumeDevID;
	protected transient LinuxVolumeCore core = null;
	protected transient FileOutputStream inhibitDismountStream;
	static LinuxNativeFileRoutines nativeFileUtil =
		new LinuxNativeFileRoutines();
	protected transient LinuxClientFile volumeRoot;
	protected transient FilePath volumeFilePath;

	/* (non-Javadoc)
	 * @see com.igeekinc.util.Volume#isBootVolume()
	 */
	public boolean isBootVolume()
	{
		loadCore();
		if (volumeRoot != null && volumeRoot.getAbsolutePath().equals("/"))
			return true;
		return false;
	}

	public LinuxVolume(LinuxVolumeCore inCore) throws IOException
	{
		core = inCore;
		doInit();
	}

	protected LinuxVolume() throws IOException
	{
		doInit();
	}

	private void readObject(ObjectInputStream oi) 
	throws IOException, ClassNotFoundException
	{
	  oi.defaultReadObject();
	  // Things like the volumeDevID can change while we're serialized on disk
	  // (and while we're in memory as well, but we'll just punt on the for the moment)
	  core = ((LinuxVolumeManager)SystemInfo.getSystemInfo().getVolumeManager()).getCoreForVolume(this);
	  update();
	}
	
	void loadCore()
	{
		if (core == null || core.online == false)
		{	
			core = ((LinuxVolumeManager)SystemInfo.getSystemInfo().getVolumeManager()).getCoreForVolume(this);
			try
			{
				update();
			}
			catch (IOException e)
			{
				//TODO Auto-generated catch block
				org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(this.getClass());
				exceptionLogger.error("Caught exception IOException", e);
			}
		}
	}
	
	protected LinuxClientFile makeVolumeRoot(String volumePath) throws IOException
	{
		return new LinuxClientFile(this, volumePath, volumePath, true);
	}
	
	void doInit() throws IOException
	{
		if (core == null)
		{
			return; // Volume is offline
		}
		deviceName = core.getMountedDevice();
		volumeName = core.volumeName;
		volumePath = core.getMountPoint();
		volumeFilePath = FilePath.getFilePath(volumePath);
		volumeRoot = makeVolumeRoot(volumePath);
		fsType = "ext2";
		
		int result;
		String rootPath = volumeRoot.getAbsolutePath();

		byte [] statsBuf = new byte[StatStructure.getBufferSize()];
		result = nativeFileUtil.lstat(rootPath, statsBuf);
		if (result != 0)
		{
			switch (result)
			{
				case LinuxErrorException.ENOENT :
					throw new FileNotFoundException(
						rootPath + " does not exist");
				default :
					throw LinuxErrorException.getErrorForNumber(result);
			}
		}
		StatStructure stats = StatStructure.getStatStructure(statsBuf);
		volumeDevID = stats.get_st_dev();
	}
	
	void update() throws IOException
	{
		if (core == null)
		{	
			volumeRoot = makeVolumeRoot(volumePath);
			return;
		}
		volumePath = core.getMountPoint();
		volumeRoot = makeVolumeRoot(volumePath);
		
		int result;
		String filePath = volumeRoot.getAbsolutePath();
		byte [] statsBuf = new byte[StatStructure.getBufferSize()];
		result = nativeFileUtil.lstat(filePath, statsBuf);
		if (result != 0)
		{
			switch (result)
			{
			case LinuxErrorException.ENOENT :
				throw new FileNotFoundException(
						filePath + " does not exist");
			default :
				throw LinuxErrorException.getErrorForNumber(result);
			}
		}
		StatStructure stats = StatStructure.getStatStructure(statsBuf);
		volumeDevID = stats.get_st_dev();		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.igeekinc.util.Volume#getClientFile(com.igeekinc.util.ClientFile,
	 *      java.lang.String)
	 */
	public ClientFile getClientFile(ClientFile parent, String fileName)
		throws IOException
	{

		int result;
		loadCore();
		if (core == null)
			return null;
		String filePath = parent.getAbsolutePath();
		// Yes, we're checking the FSID of the parent. Why? Well, because
		// mount points behave kind of strangely - they exist in both the
		// mounted filesystem and the mounted on
		// filesystem. Hence, to get a correct (or nearly correct - we can't
		// access the metadata for the directory)
		// representation of a volume, we need to return empty directories for
		// mount points. So, we refuse to return
		// any files that have a parent on a different file system,

		byte [] parentStatBuf = new byte[StatStructure.getBufferSize()];
		result = nativeFileUtil.lstat(filePath, parentStatBuf);
		
		StatStructure parentStat = StatStructure.getStatStructure(parentStatBuf);
		long parentDevID = parentStat.get_st_dev();
		if (fileName.startsWith("/"))	// Sometimes we get paths starting with "/" - remove the "/"
			fileName = fileName.substring(1);
		if (result == 0 && parentDevID != volumeDevID)
			return null;
		LinuxClientFile returnFile;
		if (result == 2) // ENOENT
		{
			returnFile =
				new LinuxClientFile(
					this,
					(LinuxClientFile) parent,
					fileName,
					false);
			; // parent does not exist
		}
		else
		{
			// Now, check ourselves to see if we are a mount point
			filePath = filePath + "/" + fileName;
			byte [] ourStatBuf = new byte[StatStructure.getBufferSize()];

			result = nativeFileUtil.lstat(filePath, ourStatBuf);
			StatStructure ourStat = StatStructure.getStatStructure(ourStatBuf);
			long fileDevID = ourStat.get_st_dev();

			if (result == 0 && fileDevID != volumeDevID)
				returnFile =
					new LinuxClientFile(
						this,
						(LinuxClientFile) parent,
						fileName,
						true);
			else
				returnFile =
					new LinuxClientFile(
						this,
						(LinuxClientFile) parent,
						fileName,
						false);
		}
		return (returnFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.igeekinc.util.Volume#getClientFile(java.lang.String,
	 *      java.lang.String)
	 */
	public ClientFile getClientFile(String basePath, String filePath)
		throws IOException
	{
		int result;
		loadCore();

		boolean isMountPoint = false;

		byte [] statsBuf = new byte[StatStructure.getBufferSize()];
		result = nativeFileUtil.lstat(filePath, statsBuf);
		if (result != 0)
		{
			switch (result)
			{
				case LinuxErrorException.ENOENT :
					break; // Do nothing for file not found
				default :
					throw LinuxErrorException.getErrorForNumber(result);
			}
		}
		else
		{
			StatStructure stats = StatStructure.getStatStructure(statsBuf);
			if (stats.get_st_dev() != volumeDevID)
			{
				long fileStDev = stats.get_st_dev();
				File thisFile = new File(filePath);
				String parentPath = thisFile.getParent();
				byte [] parentStatBuf = new byte[StatStructure.getBufferSize()];
				result = nativeFileUtil.lstat(parentPath, parentStatBuf);
				StatStructure parentStats = StatStructure.getStatStructure(statsBuf);
				if (result == 0 && parentStats.get_st_dev() == volumeDevID)
					isMountPoint = true;
				else
				{	
					if (result != 0)
						logger.error("lstat returned "+result+" for parent "+parentPath);
					else
					logger.error("Requested file '"+filePath+"' not on this device - volumeDevID = "+stats.get_st_dev()+" file dev = "+fileStDev+
							" parent dev = "+parentStats.get_st_dev());
					return null;
				}
			}
		}

		LinuxClientFile returnFile =
			new LinuxClientFile(this, basePath, filePath, isMountPoint);

		return (returnFile);
	}

	public ClientFile getRoot()
	{
		if (core == null)
			loadCore();
		
		return volumeRoot;
	}

	public String getVolumeName()
	{
		if (core != null)
		{
			return (core.volumeName);
		}
		return volumeName;
	}

	public String getDeviceName()
	{
		return (deviceName);
	}

	public long getVolumeDevID()
	{
		return (volumeDevID);
	}

	public ClientFile getRelativeClientFile(String relativePathName)
		throws IOException
	{
		return getRelativeClientFile(FilePath.getFilePath(relativePathName));
	}
	

	@Override
	public ClientFile getRelativeClientFile(FilePath partialPath)
			throws IOException 
	{
		ClientFile returnFile;
		loadCore();
		if (volumeRoot != null)
		{
			if (partialPath.getNumComponents() == 0)
				return volumeRoot;
			FilePath rootPath = volumeRoot.getFilePath();
			returnFile = getClientFile(rootPath, rootPath.getChild(partialPath));
		}
		else
		{
			returnFile = getClientFile(volumeFilePath,
					volumeFilePath.getChild(partialPath));
		}
		return(returnFile);
	}

	public boolean equals(Object checkObject)
	{
		if (!checkObject.getClass().equals(this.getClass()))
			return false;
		loadCore();
		LinuxVolume checkVolume = (LinuxVolume) checkObject;
		checkVolume.loadCore();
		if (core != null && checkVolume.core != null)	// Should share a core at this point if they're the same volume
			return(core == checkVolume.core);
		return false;
	}

	public boolean isExternal()
	{
		loadCore();
		if (core == null)
			return false;
		return core.external;
	}

	public boolean isRemovable()
	{
		loadCore();
		if (core == null)
			return false;
		return core.removable;
	}

	public long freeSpace()
	{
		StatFSStructure statFS;
		loadCore();
		byte [] statBuf = new byte[StatFSStructure.getBufferSize()];
		nativeFileUtil.statfs(volumePath, statBuf);
		statFS = StatFSStructure.getStatFSStructure(statBuf, 0);
		return statFS.getF_bfree() * statFS.getF_bsize();
	}

	public long totalSpace()
	{
		StatFSStructure statFS;
		loadCore();
		byte [] statBuf = new byte[StatFSStructure.getBufferSize()];
		nativeFileUtil.statfs(volumePath, statBuf);
		statFS = StatFSStructure.getStatFSStructure(statBuf, 0);
		return statFS.getF_blocks() * statFS.getF_bsize();
	}

	public boolean isOnline()
	{
		loadCore();
		if (core != null)
		{
			if (core.online)
				return true;
			core = null; // If core has been take offline, then we should 
		}
		//Check to see if we've been brought back online
		core = ((LinuxVolumeManager)SystemInfo.getSystemInfo().getVolumeManager()).getCoreForVolume(this);
		if (core != null && core.online)
			return true;
		return false;
	}
	/**
	 * @return Returns the fsType.
	 */
	public String getFsType()
	{
		return fsType;
	}

	public boolean isOwnershipIgnored()
	{
		return false;
	}
	/* (non-Javadoc)
	 * @see com.igeekinc.util.Volume#getBootInfo()
	 */
	public VolumeBootInfo getBootInfo()
	throws IOException
	{
	    return null;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.Volume#isBootable()
	 */
	public boolean isBootable()
	throws IOException
	{
		loadCore();
		return false;
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.Volume#makeBootable(com.igeekinc.util.VolumeBootInfo)
	 */
	public void makeBootable(VolumeBootInfo newBootInfo)
	throws IOException
	{

	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.Volume#prepForBooting()
	 */
	public void enablePermissions()
	{
		loadCore();
		// NO-OP
		
	}

	public int hashCode()
	{
		int volNameHash = volumeName.hashCode();
		int deviceNameHash = deviceName.hashCode();
		int returnHash = volNameHash ^ deviceNameHash;
		return(returnHash);
	}
	/* (non-Javadoc)
	 * @see com.igeekinc.util.Volume#allowDismount()
	 */
	public synchronized void allowDismount()
	throws IOException
	{
		if (inhibitDismountStream != null)
		{	
			inhibitDismountStream.close();
			inhibitDismountStream = null;
		}
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.Volume#inhibitDismount()
	 */
	public synchronized void inhibitDismount()
	throws IOException
	{
		// TODO - have DiskArbitrationClient also reject unmount request
		if (inhibitDismountStream == null)
		{	
			ClientFile root = getRoot();
			if (root == null)
				throw new IOException("Volume not mounted");
			ClientFile retainFile = (ClientFile)root.getChild(".indelible-retain");
			try
			{
				inhibitDismountStream = new FileOutputStream(retainFile);
				retainFile.delete();
			}
			catch (IOException e)
			{
				//TODO Auto-generated catch block
				org.apache.log4j.Logger exceptionLogger = org.apache.log4j.Logger.getLogger(this.getClass());
				exceptionLogger.warn("Could not create retain file "+retainFile.getAbsolutePath());
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.Volume#getTrashDirectory(com.igeekinc.util.User)
	 */
	public ClientFile getTrashDirectory(User user)
	{
	    return null;	// Linux doesn't have any stinking trash cans
	}

	@Override
	public long filesInUse() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRemote() {
		// TODO Auto-generated method stub
		return false;
	}
}
