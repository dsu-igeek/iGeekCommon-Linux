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
 
package com.igeekinc.util.linux.nativeifs;

import com.igeekinc.util.linux.LinuxSystemInfo;
import com.igeekinc.util.linux.PasswordStructure;

public class LinuxNativeFileRoutines
{	
	static
	{
		LinuxSystemInfo.loadNative();
	}
	
	public native int getpwnam(String userName, PasswordStructure fillInPS);
	public native int getpwuid(int userID, PasswordStructure fillInPS);
	
	public native int lstat(String path, byte [] statBuf);
	
	public native int link(String existing, String newLink);
	public native int symlink(String existing, String newLink);
	public native String readlink(String file);
	public native int utimes(String file, int accessTimeSecs, int accessTimeNSecs, int modifyTimeSecs, int modifyTimeNSecs);
	public native int rename(String src, String dest);
	public native int statfs(String volumePath, byte [] statBuf);
	public native int chmod(String path, int mode);
	public native int chown(String path, int uid, int gid);
	public native int chflags(String path, int flags);
	public native int mount(String source, String dir, String filesystemtype, int flags, byte [] data);
}
