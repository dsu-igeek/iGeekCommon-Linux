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
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

import com.igeekinc.junitext.iGeekTestCase;
import com.igeekinc.testutils.TestFilesTool;
import com.igeekinc.util.ClientFile;
import com.igeekinc.util.ClientFileIterator;
import com.igeekinc.util.ClientFileMetaData;
import com.igeekinc.util.DefaultClientFileIterator;
import com.igeekinc.util.SHA1HashID;
import com.igeekinc.util.SystemInfo;
import com.igeekinc.util.Volume;

public class LinuxClientFileTest extends iGeekTestCase
{
    public void testUTF8() throws IOException, InterruptedException
    {
        String path  = "/";

        SystemInfo.getSystemInfo();

        LinuxClientFile testDir = (LinuxClientFile)SystemInfo.getSystemInfo().getClientFileForPath(path);
        ClientFile [] testFiles = (ClientFile [])testDir.listClientFiles();
        for (int curTestFileNum =0; curTestFileNum < testFiles.length; curTestFileNum++)
        {
        	LinuxClientFile testFile = (LinuxClientFile)testFiles[curTestFileNum];
        	LinuxFileMetaData md = (LinuxFileMetaData)testFile.getMetaData();
        }
    }
    
    public void testGetParent()
    throws Exception
    {
        File testFile = File.createTempFile("testFile", "test");
        ClientFile testCF = SystemInfo.getSystemInfo().getClientFileForFile(testFile);
        
        while(testCF != null)
            testCF = testCF.getParentClientFile();
    }

    public void testGetMetadata()
    throws Exception
    {
        File testFile = new File("/");
        LinuxClientFile testCF = (LinuxClientFile) SystemInfo.getSystemInfo().getClientFileForFile(testFile);
        LinuxFileMetaData md = (LinuxFileMetaData) testCF.getMetaData();
        System.out.println("Create time = "+md.getCreateTime());
    }
    
    public static final int kTestLength = 3786;
    public void testLength() throws IOException
    {
    	File testFile = File.createTempFile("testFile", "test");
        ClientFile testCF = SystemInfo.getSystemInfo().getClientFileForFile(testFile);
        TestFilesTool.createTestFile(testCF, kTestLength);
        LinuxFileMetaData md = (LinuxFileMetaData)testCF.getMetaData();
        assertEquals(kTestLength, testCF.length());
        assertEquals(kTestLength, md.getFileLength());
    }
    public void testSymlink()
    throws Exception
    {
    	File testDir = new File("/tmp/testdir"+System.currentTimeMillis());
    	assertTrue(testDir.mkdir());
    	File testLink = new File("/tmp/testlink"+System.currentTimeMillis());
    	String [] symLinkCommands = {"/bin/ln", "-s", testDir.getAbsolutePath(), testLink.getAbsolutePath()};
    	Process symlinkProc = Runtime.getRuntime().exec(symLinkCommands);
    	assertEquals(0, symlinkProc.waitFor());
    	LinuxClientFile testDirCF = (LinuxClientFile) SystemInfo.getSystemInfo().getClientFileForFile(testDir);
    	assertTrue(testDirCF.isDirectory());
    	LinuxClientFile testLinkCF = (LinuxClientFile) SystemInfo.getSystemInfo().getClientFileForFile(testLink);
    	assertTrue(testLinkCF.exists());
    	assertFalse(testLinkCF.isDirectory());
    	assertTrue(testLinkCF.getMetaData().isSymlink());
    	assertEquals(testDir.getAbsolutePath(), testLinkCF.getMetaData().getSymlinkTarget());
    }
    /*
    public void testCopyTBitSet()
    throws Exception
    {
        ClientFile sourceFile = SystemInfo.getSystemInfo().getClientFileForPath("/.Archived_Directories\r");
        ClientFile tmpDir = SystemInfo.getSystemInfo().getClientFileForPath("/private/tmp");
        ClientFile destFile = (ClientFile)tmpDir.getChild(".Archived_Directories\r");
        if (destFile.exists())
        {
            if (!destFile.isDirectory())
                destFile.delete();
        }
        if (!destFile.exists())
            destFile.mkdir();
        ClientFileMetaData md = sourceFile.getMetaData();
        destFile.setMetaData(md);
        ClientFileMetaData md1 = destFile.getMetaData();
        ((MacOSXHFSFileMetaData)md1).setAccessMask(0777);
        destFile.setMetaData(md1);
    }
*/
    
    public static final int kNumFilesForIteratorTest = 10000;
    public void testClientFileIterator()
    throws Exception
    {
        File testDir = File.createTempFile("testDir", "test");
        LinuxClientFile testDirCF = (LinuxClientFile) SystemInfo.getSystemInfo().getClientFileForFile(testDir);
        if (testDirCF.exists())
            testDirCF.delete();
        testDirCF.mkdir();
        TestFilesTool.makeTestHierarchy(testDir, 1, 1, kNumFilesForIteratorTest);
        {
            long start = System.currentTimeMillis();
            ClientFileIterator iterator = testDirCF.iterator();
            long iteratorFinished = System.currentTimeMillis();
            int filesListed = 0;
            while (iterator.hasNext())
            {
                ClientFile curFile = iterator.next();
                ClientFileMetaData md = curFile.getMetaData();
                if (curFile.getName().startsWith("file"))
                    filesListed++;
            }
            assertEquals(kNumFilesForIteratorTest, filesListed);
            long iteratorDelta = iteratorFinished - start;
            System.out.println("Took "+iteratorDelta+" ms to create iterator");
            long delta = System.currentTimeMillis() - start;
            System.out.println("Took "+delta+" millis to iterate "+filesListed+" entries ("+((double)delta/(double)filesListed)+" ms/entry)");
        }
        
        {
            long start = System.currentTimeMillis();
            ClientFileIterator iterator = new DefaultClientFileIterator(testDirCF);
            int filesListed = 0;
            while (iterator.hasNext())
            {
                ClientFile curFile = iterator.next();
                ClientFileMetaData md = curFile.getMetaData();
                if (curFile.getName().startsWith("file"))
                    filesListed++;
            }
            assertEquals(kNumFilesForIteratorTest, filesListed);
            long delta = System.currentTimeMillis() - start;
            System.out.println("Took "+delta+" millis to iterate "+filesListed+" entries ("+((double)delta/(double)filesListed)+" ms/entry) using default iterator");
        }
        TestFilesTool.deleteTree(testDirCF);
    }
    
    public void testMountPoints() throws IOException
    {
    	LinuxClientFile root = (LinuxClientFile)SystemInfo.getSystemInfo().getClientFileForPath("/");
        ClientFileIterator iterator = root.iterator();
        while(iterator.hasNext())
        {
        	LinuxClientFile curFile = (LinuxClientFile) iterator.next();
            if (curFile.isDirectory() && curFile.isMountPoint())
                System.out.println(curFile.getAbsolutePath()+" is a mount point");
        }
        Volume [] volumes = SystemInfo.getSystemInfo().getVolumeManager().getVolumes();
        for (Volume curVolume:volumes)
        {
        	LinuxClientFile curMountPoint = (LinuxClientFile)curVolume.getRoot();
        	assertTrue(curMountPoint.isDirectory());
        	assertTrue(curMountPoint.isMountPoint());
        }
    }
    
    public void testNonASCIINames1() throws IOException
    {
    	String [] names={"\u50b3\u7d71.collection", "\u73fe\u4ee3.collection","\u7b49\u5bec\u5b57.collection","\u7db2\u9801.collection", "\u8da3\u5473.collection"};
    	HashMap<String, SHA1HashID> hashIDs = new HashMap<String, SHA1HashID>();
    	File tempDir = File.createTempFile("funky", "dir");
    	tempDir.delete();
    	tempDir.mkdir();
    	for (int curChildName = 0; curChildName < names.length; curChildName ++)
    	{
    		File mkFile = new File(tempDir, names[curChildName]);
    		hashIDs.put(names[curChildName], TestFilesTool.createTestFile(mkFile, 1024));
    	}
    	
    	ClientFile testCFDir = SystemInfo.getSystemInfo().getClientFileForPath(tempDir.getAbsolutePath());
    	ClientFile [] children = testCFDir.listClientFiles();
    	assertEquals(names.length, children.length);
    	for (int curChildNum = 0; curChildNum < children.length; curChildNum++)
    	{
    		children[curChildNum].getMetaData();
    		TestFilesTool.verifyFile(children[curChildNum], hashIDs.get(children[curChildNum].getName()), 1024L);
    	}
    }
    /*
    public void testWeirdNames() throws Exception
    {
    	ClientFile testDir = SystemInfo.getSystemInfo().getClientFileForPath("/tmp/home/mariko/indelible test/smbtest");
    	String [] children = testDir.list();
    	for (String curChild:children)
    	{
    		ClientFile curChildFile = testDir.getChild(curChild);
    		assertTrue(curChildFile.exists());
    	}
    }
    */
    
    public void testListLargeDir() throws IOException
    {
    	File testDir = new File("/cas1/CASFiles/085/836");
    	long startTime = System.currentTimeMillis();
    	File [] files = testDir.listFiles(new CASSegSerialFilter());
    	long endTime = System.currentTimeMillis();
    	long elapsedTime = endTime - startTime;
    	System.out.println("Listed "+files.length+" from "+testDir+" in "+elapsedTime+" ms");
    }
}

class FSCASSerialStorageDirectoryFilter implements FileFilter
{

    public boolean accept(File pathname)
    {
        String name = pathname.getName();
        if (name.length() == 3)
        {
            for (char checkChar:name.toCharArray())
            {
                if (!Character.isDigit(checkChar))
                    return false;
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}

class CASSegSerialFilter implements FilenameFilter
{
    public boolean accept(File dir, String name)
    {
        return (name.endsWith(".casseg"));
    }
}