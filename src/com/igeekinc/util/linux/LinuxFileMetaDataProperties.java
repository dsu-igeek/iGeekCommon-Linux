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

import java.util.Date;
import java.util.Map;

import com.igeekinc.util.ClientFileMetaData;
import com.igeekinc.util.ClientFileMetaDataProperties;

public class LinuxFileMetaDataProperties extends ClientFileMetaDataProperties
{
    public LinuxFileMetaDataProperties(LinuxFileMetaData md)
    {
        super(md);
    }

    public LinuxFileMetaDataProperties(Map<String, Object>map)
    {
    	super(map);
    }
    
    @Override
    public LinuxFileMetaData getMetaData()
    {
    	LinuxFileMetaData returnMD = new LinuxFileMetaData();
        fillInMetaData(returnMD);
        
        return returnMD;
    }

    private Date getDateForProperty(String propertyName)
    {
    	Date returnDate = (Date)get(propertyName);
    	if (returnDate == null)
    		returnDate = new Date(0);
    	return returnDate;
    }
    protected void fillInMetaData(LinuxFileMetaData returnMD)
    {
    	returnMD.setFileLength((Long)get(kLengthPropertyName));
    	if ((Boolean)get(kIsOwnerReadOnly))
    		returnMD.setOwnerReadOnly();
        Date newModifyTime = getDateForProperty(kModifyTimePropertyName);
        if (newModifyTime == null)
        	newModifyTime = new Date(0);
		returnMD.setModifyTime(newModifyTime);
        Date newAccessTime = getDateForProperty(kAccessTimePropertyName);
        if (newAccessTime == null)
        	newAccessTime = new Date(0);
		returnMD.setAccessTime(newAccessTime);
        Date newChangeTime = getDateForProperty(kChangeTimePropertyName);
        if (newChangeTime == null)
        	newChangeTime = new Date(0);
		returnMD.setChangeTime(newChangeTime);
        if (((Boolean)get(kIsOwnerReadOnly)))
            returnMD.setOwnerReadOnly();

        Integer fileType = (Integer)get(kFileTypePropertyName);
        if (fileType == null)
        	fileType = 0;
		returnMD.setFileType(fileType);
        Long deviceID = (Long)get(kDeviceIDPropertyName);
        if (deviceID == null)
        	deviceID = 0L;
		returnMD.setDeviceID(deviceID);
        Integer ownerID = (Integer)get(kOwnerIDPropertyName);
        if (ownerID == null)
        	ownerID = 0;
		returnMD.setOwner(ownerID);
        Integer groupID = (Integer)get(kGroupIDPropertyName);
        if (groupID == null)
        	groupID = 0;
		returnMD.setGroup(groupID);
        Integer accessMask = (Integer)get(kAccessMaskPropertyName);
        if (accessMask == null)
        	accessMask = 0;
		returnMD.setAccessMask(accessMask);
        Long linkCount = (Long)get(kLinkCountPropertyName);
        if (linkCount == null)
        	linkCount = 0L;
		returnMD.setLinkCount(linkCount);
        Long inodeNum = (Long)get(kInodeNumPropertyName);
        if (inodeNum == null)
        	inodeNum = 0L;
		returnMD.setInodeNum(inodeNum);
        if (returnMD.getFileType() == LinuxFileMetaData.kSymbolicLinkType)
            returnMD.setSymlinkTarget((String)get(kSymlinkTargetPropertyName));
    }
    
    public static final String kAccessTimePropertyName = "accessTime";
    public static final String kChangeTimePropertyName = "changeTime";
    public static final String kFileTypePropertyName = "fileType";
    public static final String kDeviceIDPropertyName = "deviceID";
    public static final String kOwnerIDPropertyName = "ownerID";
    public static final String kGroupIDPropertyName = "groupID";
    public static final String kAccessMaskPropertyName = "accessMask";
    public static final String kLinkCountPropertyName = "linkCount";
    public static final String kInodeNumPropertyName = "inodeNum";
    public static final String kFlagsPropertyName = "flags";
    public static final String kSymlinkTargetPropertyName = "symlinkTarget";
    public static final String kACLPropertyName = "acl";

    protected void initFromMetaData(ClientFileMetaData md)
    {
        super.initFromMetaData(md);
        LinuxFileMetaData linuxMD = (LinuxFileMetaData)md;
        put(kAccessTimePropertyName, linuxMD.getAccessTime());
        put(kChangeTimePropertyName, linuxMD.getChangeTime());
        put(kFileTypePropertyName, linuxMD.getFileType());
        put(kDeviceIDPropertyName, linuxMD.getDeviceID());
        put(kOwnerIDPropertyName, linuxMD.getOwnerID());
        put(kGroupIDPropertyName, linuxMD.getGroupID());
        put(kAccessMaskPropertyName, linuxMD.getAccessMask());
        put(kLinkCountPropertyName, linuxMD.getLinkCount());
        put(kInodeNumPropertyName, linuxMD.getInodeNum());
        if (linuxMD.getFileType() == LinuxFileMetaData.kSymbolicLinkType)
            put(kSymlinkTargetPropertyName, linuxMD.getSymlinkTarget());
        /*
        if (macOSXMD.getACL() != null)
            put(kACLPropertyName, macOSXMD.getACL().toString());*/
        //put(kTypePropertyName, macOSXMD.getType());
    }
}
