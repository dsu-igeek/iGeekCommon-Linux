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
import com.igeekinc.util.ClientFile;
import com.igeekinc.util.FilePath;
import com.igeekinc.util.SystemInfo;
import com.igeekinc.util.Volume;
import com.igeekinc.util.VolumeManager;

import junit.framework.TestCase;

public class LinuxFilePathTest extends TestCase
{
    public void testRootFilePath() throws IOException
    {
    	FilePath testRootPath = FilePath.getFilePath("/");
        Volume root = ((VolumeManager)SystemInfo.getSystemInfo().getVolumeManager()).getBootDrive();
        FilePath rootPath = root.getRoot().getFilePath();
        FilePath tmpPath = rootPath.getChild("tmp");
        ClientFile testFile = SystemInfo.getSystemInfo().getClientFileForPath(tmpPath);
        FilePath testPath = testFile.getFilePath();
        FilePath checkPath = FilePath.getFilePath("/tmp");
        assertEquals(checkPath, testPath);
    }
    /*
    public void testNormalizeSymlinks() throws IOException
    {
        LinuxClientFile tmpDir = (LinuxClientFile) SystemInfo.getSystemInfo().getClientFileForFile(SystemInfo.getSystemInfo().getTemporaryDirectory());
        LinuxFilePath tmpDirPath = (LinuxFilePath) tmpDir.getFilePath();
        LinuxFilePath normalizedPath = (LinuxFilePath) tmpDirPath.normalizeSymlinks();
        LinuxClientFile dir1 = (LinuxClientFile) tmpDir.getChild("dirLevel1");
        dir1.mkdir();
        
    }
    */
}
