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

public class LinuxVolumeCore
{
	String volumeName;
	String mountedDevice, mountPoint;
	StatFSStructure volumeInfo;
	StatStructure mountPointInfo;
	boolean external, removable;
	boolean online;
	
	public LinuxVolumeCore(StatFSStructure volumeInfo, StatStructure mountPointInfo, String volumeName, 
			String mountedDevice, String mountPoint)
	{
		this.volumeName = volumeName;
		this.volumeInfo = volumeInfo;
		this.mountedDevice = mountedDevice;
		this.mountPoint = mountPoint;
		this.mountPointInfo = mountPointInfo;
		online = true;
	}
	
	public String getVolumeName() {
		return volumeName;
	}

	public String getMountedDevice() {
		return mountedDevice;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public StatFSStructure getVolumeInfo() {
		return volumeInfo;
	}

	public StatStructure getMountPointInfo() {
		return mountPointInfo;
	}

	void setVolumeName(String inVolumeName)
	{
		volumeName = inVolumeName;
	}
	/**
	 * @return Returns the external.
	 */
	public boolean isExternal()
	{
		return external;
	}

	/**
	 * @param external The external to set.
	 */
	public void setExternal(boolean external)
	{
		this.external = external;
	}

	/**
	 * @return Returns the removable.
	 */
	public boolean isRemovable()
	{
		return removable;
	}

	/**
	 * @param removable The removable to set.
	 */
	public void setRemovable(boolean removable)
	{
		this.removable = removable;
	}
	
	public boolean equals(Object checkObject)
	{
		if (!(checkObject instanceof LinuxVolumeCore))
			return false;
		LinuxVolumeCore checkCore = (LinuxVolumeCore)checkObject;
		if (checkCore.mountPoint.equals(mountPoint))
				return true;
		return false;
	}

	public boolean isOnline()
	{
		return online;
	}
	
	public void setOnline(boolean online)
	{
		this.online = online;
	}
	

}
