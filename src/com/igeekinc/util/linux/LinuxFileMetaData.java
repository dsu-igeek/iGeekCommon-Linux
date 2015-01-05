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

import java.io.Serializable;
import java.util.Date;

import com.igeekinc.util.ClientFileMetaData;
import com.igeekinc.util.SystemInfo;
import com.igeekinc.util.User;
import com.igeekinc.util.exceptions.ForkNotFoundException;
import com.igeekinc.util.msgpack.ClientFileMetaDataMsgPack;
import com.igeekinc.util.unix.UnixDate;

public class LinuxFileMetaData extends ClientFileMetaData implements Serializable
{
    static final long serialVersionUID = -1803218348393039376L;
    private int     fileType;
    private long	deviceID;
    protected boolean changeTimeChanged=false, modifyTimeChanged=false, /*changeTimeChanged = false,*/ accessTimeChanged=false;
    protected long fileLength;
    private Date   changeTime, modifyTime, accessTime;
    
    protected int     ownerID, groupID, accessMask;
    private long    linkCount;
    private long    inodeNum;
    
    protected boolean groupIDChanged, ownerIDChanged, accessMaskChanged, flagsChanged;
    private String symlinkTarget;
    public static final int kRegularFileType=1;
    public static final int kDirectoryType=2;
    public static final int kBlockDeviceType=3;
    public static final int kCharacterDeviceType=4;
    public static final int kSymbolicLinkType=5;
    public static final int kSocketType=6;
    public static final int kFIFOType=7;
    public static final int kBadFileType=8;
    public static final int kSTRFileType=9;
    public static final int kComplexFileType=10;
    
    public static final int UF_SETTABLE   = 0x0000ffff;      /* mask of owner changeable flags */
    public static final int UF_NODUMP     = 0x00000001;      /* do not dump file */
    public static final int UF_IMMUTABLE  = 0x00000002;      /* file may not be changed */
    public static final int UF_APPEND     = 0x00000004;      /* writes to file may only append */
    public static final int UF_OPAQUE     = 0x00000008;      /* directory is opaque wrt. union */
    /* 
     * Super-user changeable flags.
     */
    public static final int SF_SETTABLE   = 0xffff0000;      /* mask of superuser changeable flags */
    public static final int SF_ARCHIVED   = 0x00010000;      /* file is archived */
    public static final int SF_IMMUTABLE  = 0x00020000;      /* file may not be changed */
    public static final int  SF_APPEND    = 0x00040000;      /* writes to file may only append */
    
    public LinuxFileMetaData()
    {
    }
    public long getDeviceID()
    {
        return deviceID;
    }
    
    public void setDeviceID(long deviceID)
    {
    	this.deviceID = deviceID;
    }
    
    public User getOwner()
    {
        try
        {
            return ((LinuxSystemInfo)SystemInfo.getSystemInfo()).getUserInfoForUID(ownerID);
        }
        catch (com.igeekinc.util.exceptions.UserNotFoundException e)
        {
            return null;
        }
    }
    
    public void setFileLength(long fileLength)
    {
    	this.fileLength = fileLength;
    }
    
    public void setGroup(int newGroupID)
    {
        if (groupID != newGroupID)
        {
            groupIDChanged = true;
            groupID = newGroupID;
        }
    }
    
    public void setOwner(int newOwnerID)
    {
        if (ownerID != newOwnerID)
        {
            ownerIDChanged = true;
            ownerID = newOwnerID;
        }
    }
    
    public int getOwnerID()
    {
        return ownerID;
    }
    
    public int getGroupID()
    {
        return groupID;
    }
    
    public void setOwner(User newUser)
    {
        LinuxUser setUser = (LinuxUser)newUser;
        if (ownerID != setUser.getUID())
        {
            ownerIDChanged = true;
            ownerID = setUser.getUID();
        }
    }
    
    public void setModifyTime(Date modifyTime)
    {
    	if (modifyTime == null)
    		throw new IllegalArgumentException("modifyTime can't be null");
        setModifyTime(new UnixDate(modifyTime));
    }
    
    public void setChangeTime(Date changeTime)
    {
    	if (changeTime == null)
    		throw new IllegalArgumentException("changeTime can't be null");
        setChangeTime(new UnixDate(changeTime));
    }
    
    public void setModifyTime(UnixDate modifyTime)
    {
    	if (modifyTime == null)
    		throw new IllegalArgumentException("modifyTime can't be null");
        this.modifyTime = modifyTime;
        modifyTimeChanged = true;
    }
    
    public void setChangeTime(UnixDate changeTime)
    {
    	if (changeTime == null)
    		throw new IllegalArgumentException("changeTime can't be null");
        this.changeTime = changeTime;
        changeTimeChanged = true;
    }
    public void clearChangeRecord()
    {
        modifyTimeChanged = false;
        changeTimeChanged = false;
        ownerIDChanged = false;
        groupIDChanged = false;
        accessMaskChanged = false;
        flagsChanged = false;
    }
    
    public void setFromMetaData(LinuxFileMetaData newData)
    {
        setOwner(newData.getOwnerID());
        setGroup(newData.getGroupID());
        setAccessMask(newData.getAccessMask());
        setAccessTime(newData.getAccessTime());
        setChangeTime(newData.getChangeTime());
        setModifyTime(newData.getModifyTime());
        
    }
    
    public int getFileType()
    {
        return fileType;
    }
    
    public int getAccessMask()
    {
        return accessMask;
    }
    
    public void setAccessMask(int newAccessMask)
    {
        if (accessMask != newAccessMask)
        {
            accessMask = (newAccessMask & ~LinuxOSConstants.S_IFMT);
            accessMaskChanged = true;
        }
    }
    
    protected void setFromStatStructure(StatStructure newInfo)
    {
        UnixDate changeTime, modifyTime, accessTime;
        changeTime = new UnixDate(newInfo.get_st_ctime(), newInfo.get_st_ctimensec());
        modifyTime = new UnixDate(newInfo.get_st_mtime(), newInfo.get_st_mtimensec());
        accessTime = new UnixDate(newInfo.get_st_atime(), newInfo.get_st_atimensec());
        
        this.changeTime = changeTime;
        this.modifyTime = modifyTime;
        this.accessTime = accessTime;
        
        linkCount = newInfo.get_st_nlink();
        ownerID = newInfo.get_st_uid();
        groupID = newInfo.get_st_gid();
        deviceID = newInfo.get_st_dev();
        //inodeNum = newInfo.get_st_ino();
        switch (newInfo.get_st_mode() & LinuxOSConstants.S_IFMT)
        {
        case LinuxOSConstants.S_IFIFO:		/* named pipe (fifo) */
            fileType = kFIFOType;
        break;
        case LinuxOSConstants.S_IFCHR:		/* character special */
            fileType = kCharacterDeviceType;
        break;
        case LinuxOSConstants.S_IFDIR:		/* directory */
            fileType = kDirectoryType;
        break;
        case LinuxOSConstants.S_IFBLK:		/* block special */
            fileType = kBlockDeviceType;
        break;
        case LinuxOSConstants.S_IFREG:		/* regular */
            fileType = kRegularFileType;
        break;
        case LinuxOSConstants.S_IFLNK:		/* symbolic link */
            fileType = kSymbolicLinkType;
        break;
        case LinuxOSConstants.S_IFSOCK:		/* socket */
            fileType = kSocketType;
        break;
        case LinuxOSConstants.S_IFWHT:		/* whiteout */
            break;
        }
        accessMask = newInfo.get_st_mode() & 07777;
        fileLength = newInfo.get_st_size();
        inodeNum = newInfo.get_st_ino();
    }
    
    protected UnixDate [] getUpdatedTimes(boolean always)
    {
        if (always || this.accessTimeChanged || this.modifyTimeChanged)
        {
            UnixDate returnTimes [] = new UnixDate[2];
            returnTimes[0] = (UnixDate)getAccessTime();
            returnTimes[1] = (UnixDate)getModifyTime();
            return(returnTimes);
        }
        else
            return null;
    }
    
    protected Integer getFileMode()
    {
        return null;
    }
    protected void setSymlinkTarget(String newTarget)
    {
        symlinkTarget = newTarget;
    }
    
    public String getSymlinkTarget()
    {
        return (symlinkTarget);
    }
    
    /* (non-Javadoc)
     * @see com.igeekinc.util.ClientFileMetaData#setOwnerReadOnly()
     */
    public void setOwnerReadOnly()
    {
        setAccessMask(0600);
    }
    
    
    protected StringBuffer getInfoStringBuffer()
    {
        StringBuffer returnBuffer = new StringBuffer();
        returnBuffer.append("[(LinuxFileMetaData) ft=");
        returnBuffer.append(fileType);
        returnBuffer.append(",dID=");
        returnBuffer.append(deviceID);
        returnBuffer.append(",oid=");
        returnBuffer.append(ownerID);
        if (ownerIDChanged)
            returnBuffer.append("(C)");
        returnBuffer.append(",gid=");
        returnBuffer.append(groupID);
        if (groupIDChanged)
            returnBuffer.append("(C)");
        returnBuffer.append(",am=");
        returnBuffer.append(Integer.toString(accessMask, 8));
        returnBuffer.append(",lc=");
        returnBuffer.append(linkCount);
        returnBuffer.append(",in=");
        returnBuffer.append(inodeNum);
        if (fileType == kSymbolicLinkType)
        {
            returnBuffer.append(",sl=");
            returnBuffer.append(symlinkTarget);
        }
        returnBuffer.append("]");
        return(returnBuffer);
    }
    
    public Date getAccessTime()
    {
        return accessTime;
    }
    
    public void setAccessTime(Date accessTime)
    {
        setAccessTime(new UnixDate(accessTime));
    }
    
    public void setAccessTime(UnixDate newAccessTime)
    {
        accessTime = newAccessTime;
        accessTimeChanged = true;
    }
    public Date getChangeTime()
    {
        return changeTime;
    }
    
	@Override
	public Date getCreateTime()
	{
		return getChangeTime();	// This is the default behavior for FS's that don't store create time
	}
	
    public long getFileLength()
    {
        return fileLength;
    }
    public Date getModifyTime()
    {
        return modifyTime;
    }
	public long getLinkCount()
	{
		return linkCount;
	}
	public void setLinkCount(long linkCount)
	{
		this.linkCount = linkCount;
	}
	public long getInodeNum()
	{
		return inodeNum;
	}
	public void setInodeNum(long inodeNum)
	{
		this.inodeNum = inodeNum;
	}
	@Override
	public long getForkLength(String forkName) throws ForkNotFoundException 
	{
		if (forkName.equals("data"))
			return fileLength;
		return 0;
	}
	@Override
	public LinuxFileMetaDataProperties getProperties() 
	{
		return new LinuxFileMetaDataProperties(this);
	}
	@Override
	public boolean isOwnerReadOnly() 
	{
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setFileType(int fileType) 
	{
		this.fileType = fileType;
	}

	@Override
	public boolean isDirectory() 
	{
		return fileType == kDirectoryType;
	}
	
	@Override
	public boolean isSymlink()
	{
		return fileType == kSymbolicLinkType;
	}
	@Override
	public boolean isRegularFile() 
	{
		return fileType == kRegularFileType;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + accessMask;
		result = prime * result
				+ ((accessTime == null) ? 0 : accessTime.hashCode());
		result = prime * result
				+ ((changeTime == null) ? 0 : changeTime.hashCode());
		result = prime * result + (int) (deviceID ^ (deviceID >>> 32));
		result = prime * result + (int) (fileLength ^ (fileLength >>> 32));
		result = prime * result + fileType;
		result = prime * result + groupID;
		result = prime * result + (groupIDChanged ? 1231 : 1237);
		result = prime * result + (int) (inodeNum ^ (inodeNum >>> 32));;
		result = prime * result + (int) (linkCount ^ (linkCount >>> 32));
		result = prime * result
				+ ((modifyTime == null) ? 0 : modifyTime.hashCode());
		result = prime * result + ownerID;
		result = prime * result
				+ ((symlinkTarget == null) ? 0 : symlinkTarget.hashCode());
		return result;
	}
	
	 public boolean equals(Object obj)
	    {
	        if (this == obj)
	            return true;
	        if (!super.equals(obj))
	            return false;
	        if (!(obj instanceof LinuxFileMetaData))
	            return false;
	        final LinuxFileMetaData other = (LinuxFileMetaData) obj;
	        // Our looseEquals ignores link count on directories and inode num on all files (has been called by super.equals)
	        if (linkCount != other.linkCount)
	            return false;
	        if (inodeNum != other.inodeNum)
	            return false;
	        return true;
	    }
	    
	    /**
	     * @param other
	     * @return
	     */
	    public boolean looseEquals(ClientFileMetaData otherMD)
	    {
	        if (!(otherMD instanceof LinuxFileMetaData))
	            return false;
	        LinuxFileMetaData other = (LinuxFileMetaData)otherMD;
	        if (fileLength != other.fileLength)
	            return false;
	        if (modifyTime == null)
	        {
	            if (other.modifyTime != null)
	                return false;
	        } else if (!modifyTime.equals(other.modifyTime))
	            return false;
	        if (fileType != other.fileType)
	            return false;
	        /*if (deviceID != other.deviceID)
	            return false;*/
	        if (ownerID != other.ownerID)
	            return false;
	        if (groupID != other.groupID)
	            return false;
	        if (accessMask != other.accessMask)
	            return false;

	        if (fileType == kSymbolicLinkType)
	        {
	        	if (symlinkTarget == null)
	        	{
	        		if (other.symlinkTarget != null && other.symlinkTarget.length() > 0)
	        			return false;
	        	} else if (!symlinkTarget.equals(other.symlinkTarget))
	        		return false;
	        }
	        return true;
	    }
	    
		@Override
		public ClientFileMetaDataMsgPack getMDMsgPack()
		{
			return new LinuxFileMetaDataMsgPack(this);
		}	
}
