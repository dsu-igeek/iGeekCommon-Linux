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

import junit.framework.Assert;
import junit.framework.TestCase;

import com.igeekinc.util.EthernetID;
import com.igeekinc.util.Group;
import com.igeekinc.util.SystemInfo;
import com.igeekinc.util.User;
import com.igeekinc.util.exceptions.GroupNotFoundException;
import com.igeekinc.util.exceptions.UserNotFoundException;

public class LinuxSystemInfoTest extends TestCase
{
    public void testA()
    {
        SystemInfo.getSystemInfo();
    }
    
    public void testgetUserInfoForUID() throws UserNotFoundException
    {
        User userInfo = ((LinuxSystemInfo)SystemInfo.getSystemInfo()).getUserInfoForUID(0);
    }
    
    public void testgetGroupInfoForGID() throws GroupNotFoundException
    {
        Group groupInfo = ((LinuxSystemInfo)SystemInfo.getSystemInfo()).getGroupInfoForGID(0);
        System.out.println(groupInfo);
    }
    
    public void testGetSystemBootTime()
    {
        Date bootTime = ((LinuxSystemInfo)SystemInfo.getSystemInfo()).getSystemBootTime();
        System.out.println("bootTime = "+bootTime);
    }
    
    public void testGetEthernetID()
    {
    	EthernetID ethernetID = ((LinuxSystemInfo)SystemInfo.getSystemInfo()).getEthernetID();
    	Assert.assertNotNull(ethernetID);
    	System.out.println("ethernet id = " + ethernetID);
    }
}
