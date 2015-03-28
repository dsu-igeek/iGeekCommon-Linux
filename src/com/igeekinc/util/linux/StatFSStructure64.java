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

public class StatFSStructure64 extends StatFSStructure
{
	public static final int f_typeOffset = 0;
	public static final int f_bsizeOffset = 8;
	public static final int f_blocksOffset = 16;
	public static final int f_bfreeOffset = 24;
	public static final int f_bavailOffset = 32;
	public static final int f_filesOffset = 40;
	public static final int f_ffreeOffset = 48;
	public static final int f_fsidOffset = 56;
	public static final int f_namelenOffset = 64;
	public static final int f_frsizeOffset = 72;
	public static final int f_spareOffset = 88;
	
	public static final int st_bufsize = 120;
	
	static final int getBufferSize64()
	{
		return st_bufsize;
	}
	
	protected StatFSStructure64(byte [] buffer, int offset)
	{
		super(buffer, offset);
		if (buffer.length - offset < st_bufsize)
			throw new IllegalArgumentException("buffer length = " + buffer.length +", offset = "+offset+", st_bufsize = "+
					st_bufsize+ ", minimum expected length = "+offset + st_bufsize);
	}

	@Override
	public long getF_bavail() 
	{
		return getLongAtOffset(f_bavailOffset);
	}

	@Override
	public long getF_bfree()
	{
		return getLongAtOffset(f_bfreeOffset);
	}

	@Override
	public long getF_blocks() 
	{
		return getLongAtOffset(f_blocksOffset);
	}

	@Override
	public long getF_bsize() 
	{
		return getLongAtOffset(f_bsizeOffset);
	}

	@Override
	public long getF_ffree() 
	{
		return getLongAtOffset(f_ffreeOffset);
	}

	@Override
	public long getF_files() 
	{
		return getLongAtOffset(f_filesOffset);
	}

	@Override
	public long getF_fsid() 
	{
		return getLongAtOffset(f_fsidOffset);
	}
}
