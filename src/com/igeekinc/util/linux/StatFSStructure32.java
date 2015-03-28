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

public class StatFSStructure32 extends StatFSStructure 
{
	public static final int f_typeOffset = 0;
	public static final int f_bsizeOffset = 4;
	public static final int f_blocksOffset = 8;
	public static final int f_bfreeOffset = 12;
	public static final int f_bavailOffset = 16;
	public static final int f_filesOffset = 20;
	public static final int f_ffreeOffset = 24;
	public static final int f_fsidOffset = 28;
	public static final int f_namelenOffset = 36;
	public static final int f_frsizeOffset = 40;
	public static final int f_spareOffset = 48;
	
	public static final int st_bufsize = 64;
	
	static final int getBufferSize32()
	{
		return st_bufsize;
	}
	
	protected StatFSStructure32(byte [] buffer, int offset)
	{
		super(buffer, offset);
		if (buffer.length - offset < st_bufsize)
			throw new IllegalArgumentException("buffer length = " + buffer.length +", offset = "+offset+", st_bufsize = "+
					st_bufsize+ ", minimum expected length = "+offset + st_bufsize);
	}

	@Override
	public long getF_bavail() 
	{
		return getIntAtOffset(f_bavailOffset);
	}

	@Override
	public long getF_bfree()
	{
		return getIntAtOffset(f_bfreeOffset);
	}

	@Override
	public long getF_blocks() 
	{
		return getIntAtOffset(f_blocksOffset);
	}

	@Override
	public long getF_bsize() 
	{
		return getIntAtOffset(f_bsizeOffset);
	}

	@Override
	public long getF_ffree() 
	{
		return getIntAtOffset(f_ffreeOffset);
	}

	@Override
	public long getF_files() 
	{
		return getIntAtOffset(f_filesOffset);
	}

	@Override
	public long getF_fsid() 
	{
		return getLongAtOffset(f_fsidOffset);
	}
	
}
