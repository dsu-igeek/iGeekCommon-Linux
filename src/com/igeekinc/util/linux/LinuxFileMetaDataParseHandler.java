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

import java.util.ArrayList;
import java.util.Date;

import com.igeekinc.util.xmlserial.XMLFieldParseInfo;
import com.igeekinc.util.xmlserial.XMLToObjectHandler;
import com.igeekinc.util.xmlserial.parsehandlers.EncodedStringParseHandler;
import com.igeekinc.util.xmlserial.parsehandlers.IntegerParseHandler;
import com.igeekinc.util.xmlserial.parsehandlers.LongParseHandler;

public class LinuxFileMetaDataParseHandler extends XMLToObjectHandler<LinuxFileMetaData>
{
	private LongParseHandler dataLengthParseHandler;
	private LongParseHandler modifyTimeParseHandler;
	private LongParseHandler changeTimeParseHandler;
	private LongParseHandler accessTimeParseHandler;
	private IntegerParseHandler fileTypeParseHandler;
    private LongParseHandler deviceIDParseHandler;
    private IntegerParseHandler ownerIDParseHandler;
    private IntegerParseHandler groupIDParseHandler;
    private IntegerParseHandler accessMaskParseHandler;
    private IntegerParseHandler linkCountParseHandler;
    private IntegerParseHandler inodeNumParseHandler;
    private EncodedStringParseHandler symlinkTargetParseHandler;

    
    public LinuxFileMetaDataParseHandler()
    {
        ArrayList<XMLFieldParseInfo> mappingList = setupMappings();
        XMLFieldParseInfo [] mappings = new XMLFieldParseInfo[mappingList.size()];
        mappings = mappingList.toArray(mappings);
        setMappings(mappings);
    }
    
    protected ArrayList<XMLFieldParseInfo> setupMappings()
    {
    	ArrayList<XMLFieldParseInfo> mappingsList = new ArrayList<XMLFieldParseInfo>();
    	dataLengthParseHandler = new LongParseHandler();
    	modifyTimeParseHandler = new LongParseHandler();
    	changeTimeParseHandler = new LongParseHandler();
    	accessTimeParseHandler = new LongParseHandler();
        fileTypeParseHandler = new IntegerParseHandler();
        deviceIDParseHandler = new LongParseHandler();
        ownerIDParseHandler = new IntegerParseHandler();
        groupIDParseHandler = new IntegerParseHandler();
        accessMaskParseHandler = new IntegerParseHandler();
        linkCountParseHandler = new IntegerParseHandler();
        inodeNumParseHandler = new IntegerParseHandler();
        symlinkTargetParseHandler = new EncodedStringParseHandler();
        
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kDataLengthFieldName, dataLengthParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kModifyTimeFieldName, modifyTimeParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kChangeTimeFieldName, changeTimeParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kAccessTimeFieldName, accessTimeParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kFileTypeFieldName, fileTypeParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kDeviceIDFieldName, deviceIDParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kOwnerIDFieldName, ownerIDParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kGroupIDFieldName, groupIDParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kAccessMaskFieldName, accessMaskParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kLinkCountFieldName, linkCountParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kInodeNumFieldName, inodeNumParseHandler, false));
        mappingsList.add(new XMLFieldParseInfo(LinuxFileMetaDataSerializeHandler.kSymlinkTargetFieldName, symlinkTargetParseHandler, false));
        return mappingsList;
    }
    public LinuxFileMetaData getObject()
    {
    	LinuxFileMetaData returnMD = new LinuxFileMetaData();
    	returnMD.setFileLength(dataLengthParseHandler.getValue());
    	returnMD.setModifyTime(new Date(modifyTimeParseHandler.getValue()));
    	returnMD.setChangeTime(new Date(changeTimeParseHandler.getValue()));
    	returnMD.setAccessTime(new Date(accessTimeParseHandler.getValue()));
        returnMD.setFileType(fileTypeParseHandler.getValue());
        returnMD.setDeviceID(deviceIDParseHandler.getValue());
        returnMD.setOwner(ownerIDParseHandler.getValue());
        returnMD.setGroup(groupIDParseHandler.getValue());
        returnMD.setAccessMask(accessMaskParseHandler.getValue());
        returnMD.setLinkCount(linkCountParseHandler.getValue());
        returnMD.setInodeNum(inodeNumParseHandler.getValue());
        returnMD.setSymlinkTarget(symlinkTargetParseHandler.getValue());
        return returnMD;
    }
}
