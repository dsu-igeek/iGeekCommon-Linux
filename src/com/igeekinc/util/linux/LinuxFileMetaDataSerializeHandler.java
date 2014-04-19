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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.igeekinc.util.pauseabort.AbortedException;
import com.igeekinc.util.pauseabort.PauserControlleeIF;
import com.igeekinc.util.xmlserial.FieldEntry;
import com.igeekinc.util.xmlserial.ObjectToXMLSerializer;
import com.igeekinc.util.xmlserial.XMLObjectSerializeInfo;
import com.igeekinc.util.xmlserial.serializers.EncodedStringSerializeHandler;
import com.igeekinc.util.xmlserial.serializers.IntegerSerializeHandler;
import com.igeekinc.util.xmlserial.serializers.LongSerializeHandler;

public class LinuxFileMetaDataSerializeHandler extends ObjectToXMLSerializer<LinuxFileMetaData>
{
    public static final String kDataLengthFieldName = "dataLength";
    public static final String kFileTypeFieldName = "fileType";
    public static final String kDeviceIDFieldName = "deviceID";
    public static final String kOwnerIDFieldName = "ownerID";
    public static final String kGroupIDFieldName="groupID";
    public static final String kModifyTimeFieldName = "modifyTime";
    public static final String kChangeTimeFieldName = "changeTime";
    public static final String kAccessTimeFieldName = "accessTime";
    public static final String kAccessMaskFieldName = "accessMask";
    public static final String kLinkCountFieldName = "linkCount";
    public static final String kInodeNumFieldName = "inodeNum";
    public static final String kSymlinkTargetFieldName = "symlinkTarget";
    
    private static XMLObjectSerializeInfo [] mappings= {
        new XMLObjectSerializeInfo(kDataLengthFieldName, new LongSerializeHandler()),
        new XMLObjectSerializeInfo(kFileTypeFieldName, new IntegerSerializeHandler()),
        new XMLObjectSerializeInfo(kDeviceIDFieldName, new LongSerializeHandler()),
        new XMLObjectSerializeInfo(kOwnerIDFieldName, new IntegerSerializeHandler()),
        new XMLObjectSerializeInfo(kGroupIDFieldName, new IntegerSerializeHandler()),
        new XMLObjectSerializeInfo(kModifyTimeFieldName, new LongSerializeHandler()),
        new XMLObjectSerializeInfo(kChangeTimeFieldName, new LongSerializeHandler()),
        new XMLObjectSerializeInfo(kAccessTimeFieldName, new LongSerializeHandler()),
        new XMLObjectSerializeInfo(kAccessMaskFieldName, new IntegerSerializeHandler()),
        new XMLObjectSerializeInfo(kLinkCountFieldName, new LongSerializeHandler()),
        new XMLObjectSerializeInfo(kInodeNumFieldName, new LongSerializeHandler()),
        new XMLObjectSerializeInfo(kSymlinkTargetFieldName, new EncodedStringSerializeHandler())
        
    };
    
    private static XMLObjectSerializeInfo [] appendMappings(XMLObjectSerializeInfo [] mappingsToAppend)
    {
        XMLObjectSerializeInfo [] returnMappings = new XMLObjectSerializeInfo[mappings.length + mappingsToAppend.length];
        System.arraycopy(mappings, 0, returnMappings, 0, mappings.length);
        System.arraycopy(mappingsToAppend, 0, returnMappings, mappings.length, mappingsToAppend.length);
        return returnMappings;
    }
    
    public LinuxFileMetaDataSerializeHandler()
    {
        super(mappings);
    }

    protected LinuxFileMetaDataSerializeHandler(XMLObjectSerializeInfo [] mappingsToAppend)
    {
        super(appendMappings(mappingsToAppend));
    }
    
    public void serializeObject(String fieldName, ContentHandler xmlHandler,
            LinuxFileMetaData objectToSerialize, PauserControlleeIF pauser)
            throws SAXException, AbortedException, IOException
    {
        xmlHandler.startElement("", "", fieldName, new AttributesImpl());
        ArrayList<FieldEntry> fields = getFieldEntries(objectToSerialize);
        serializeFields(xmlHandler, fields, pauser);
        xmlHandler.endElement("", "", fieldName);
    }
    /**
     * @param serializeMD
     */
    protected ArrayList<FieldEntry> getFieldEntries(LinuxFileMetaData serializeMD)
    {
        ArrayList<FieldEntry> fields = new ArrayList<FieldEntry>();
        fields.add(new FieldEntry(kDataLengthFieldName, serializeMD.fileLength));
        fields.add(new FieldEntry(kFileTypeFieldName, serializeMD.getFileType()));
        fields.add(new FieldEntry(kDeviceIDFieldName, serializeMD.getDeviceID()));
        fields.add(new FieldEntry(kOwnerIDFieldName, serializeMD.getOwnerID()));
        fields.add(new FieldEntry(kGroupIDFieldName, serializeMD.getGroupID()));
        fields.add(new FieldEntry(kAccessMaskFieldName, serializeMD.getAccessMask()));
        Date modifyTime = serializeMD.getModifyTime();
		long modifyTimeLong;
		if (modifyTime != null)
			modifyTimeLong = modifyTime.getTime();
		else
			modifyTimeLong = 0;
		fields.add(new FieldEntry(kModifyTimeFieldName, modifyTimeLong));
        Date changeTime = serializeMD.getChangeTime();
		long changeTimeLong;
		if (changeTime != null)
			changeTimeLong = changeTime.getTime();
		else
			changeTimeLong = 0;
		fields.add(new FieldEntry(kChangeTimeFieldName, changeTimeLong));
        Date accessTime = serializeMD.getAccessTime();
		long accessTimeLong;
		if (accessTime != null)
			accessTimeLong = accessTime.getTime();
		else
			accessTimeLong = 0;
		fields.add(new FieldEntry(kAccessTimeFieldName, accessTimeLong));
        fields.add(new FieldEntry(kLinkCountFieldName, serializeMD.getLinkCount()));
        fields.add(new FieldEntry(kInodeNumFieldName, serializeMD.getInodeNum()));
        fields.add(new FieldEntry(kSymlinkTargetFieldName, serializeMD.getSymlinkTarget()));
        return fields;
    }
}
