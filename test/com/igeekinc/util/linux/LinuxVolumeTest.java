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

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.igeekinc.util.SystemInfo;
import com.igeekinc.util.Volume;

public class LinuxVolumeTest extends TestCase 
{
    public void testFreeSpace() throws IOException
    {
        File testFile = new File("/data01");
        long javaFreeSpace = testFile.getFreeSpace();
        Volume rootVolume = SystemInfo.getSystemInfo().getVolumeManager().getVolumeForPath(testFile.getAbsolutePath());
        long freeSpace = rootVolume.freeSpace();
		assertEquals(javaFreeSpace, freeSpace);
    }
}
