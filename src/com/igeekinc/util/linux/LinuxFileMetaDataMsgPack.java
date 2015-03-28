/*
 * Copyright 2002-2014 iGeek, Inc.
 * All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igeekinc.util.linux;

import org.msgpack.annotation.Message;

import com.igeekinc.util.msgpack.ClientFileMetaDataMsgPack;
import com.igeekinc.util.unix.UnixDate;

@Message
public class LinuxFileMetaDataMsgPack extends ClientFileMetaDataMsgPack
{
    public int     fileType;
    public long		deviceID;
    public long fileLength;
    public long   changeTime, modifyTime, accessTime;
    
    public int     ownerID, groupID, accessMask;
    public long    linkCount;
    public long    inodeNum;
    
    public String symlinkTarget;

    public LinuxFileMetaDataMsgPack()
    {
    	// For message pack
    }
    
    public LinuxFileMetaDataMsgPack(LinuxFileMetaData md)
    {
    	fileType = md.getFileType();
    	deviceID = md.getDeviceID();
    	fileLength = md.getFileLength();
    	changeTime = md.getChangeTime().getTime();
    	modifyTime = md.getModifyTime().getTime();
    	accessTime = md.getAccessTime().getTime();
    	ownerID = md.getOwnerID();
    	groupID = md.getGroupID();
    	accessMask = md.getAccessMask();
    	linkCount = md.getLinkCount();
    	inodeNum = md.getInodeNum();
    	symlinkTarget = md.getSymlinkTarget();
    }

	@Override
	public LinuxFileMetaData getObject()
	{
		LinuxFileMetaData returnMD = new LinuxFileMetaData();
		returnMD.setFileType(fileType);
		returnMD.setDeviceID(deviceID);
		returnMD.setFileLength(fileLength);
		returnMD.setChangeTime(new UnixDate(changeTime));
		returnMD.setModifyTime(new UnixDate(modifyTime));
		returnMD.setAccessTime(new UnixDate(accessTime));
		returnMD.setOwner(ownerID);
		returnMD.setGroup(groupID);
		returnMD.setAccessMask(accessMask);
		returnMD.setLinkCount(linkCount);
		returnMD.setInodeNum(inodeNum);
		returnMD.setSymlinkTarget(symlinkTarget);
		return returnMD;
	}
}