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

import com.igeekinc.util.linux.PasswordStructure;
import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LibC extends Library {
	LibC INSTANCE = (LibC) Native.loadLibrary("c", LibC.class);
	
	public int setuid(int newUID);
	public int seteuid(int newEUID);
	public int setgid(int newGID);
	public int setegid(int newEGID);
	public int getuid();
	public int geteuid();
	public int getgid();
	public int getegid();
	PasswordStructure getpwnam(String user);
	PasswordStructure getpwuid(int uid);
}
