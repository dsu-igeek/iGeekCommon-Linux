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

import com.igeekinc.util.BufferStructure;
import com.igeekinc.util.SystemInfo;

public abstract class StatStructure extends BufferStructure
{
	public static StatStructure getStatStructure(byte [] data)
	{
		return getStatStructure(data, 0);
	}
	
	public static StatStructure getStatStructure(byte [] data, int offset)
	{
		if (SystemInfo.is32BitVM())
			return new StatStructure32(data, offset);
		else
			return new StatStructure64(data, offset);
	}

	public static int getBufferSize()
	{
		if (SystemInfo.is32BitVM())
			return StatStructure32.getBufferSize32();
		else
			return StatStructure64.getBufferSize64();
	}
	
	public StatStructure()
	{
		super(null, 0, 0);
	}
	
	public StatStructure(byte [] buffer, int offset)
	{
		super(buffer, offset, buffer.length - offset);
	}

	public abstract long get_st_dev();
	public abstract int get_st_mode();
	public abstract long get_st_nlink();
	public abstract int get_st_uid();
	public abstract int get_st_gid();
	public abstract long get_st_rdev();
	public abstract long get_st_atime();
	public abstract long get_st_atimensec();
	public abstract long get_st_mtime();
	public abstract long get_st_mtimensec();
	public abstract long get_st_ctime();
	public abstract long get_st_ctimensec();
	public abstract long get_st_size();
	public abstract long get_st_blocks();
	public abstract long get_st_blksize();
	public abstract long get_st_ino();
	
	public String toString()
	{
		return "st_dev = "+get_st_dev()+" st_mode= "+get_st_mode()+" st_nlink = "+get_st_nlink()+" st_uid = "+get_st_uid()+
				" st_gid = "+get_st_gid()+" st_rdev = "+get_st_rdev()+" st_atime = "+get_st_atime()+" st_atimensec = "+get_st_atimensec()+
				" st_mtime = "+get_st_mtime()+" st_mtimensec = "+get_st_mtimensec()+" st_ctime = "+get_st_ctime()+" st_ctimensec = "+get_st_ctimensec()+
				" st_size = "+get_st_size() + " st_blocks = "+get_st_blocks()+" st_blksize = "+get_st_blksize()+" st_ino = "+get_st_ino();
	}
}