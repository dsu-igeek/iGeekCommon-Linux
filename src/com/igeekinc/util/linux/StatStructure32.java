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

public class StatStructure32 extends StatStructure
{
	public static final int st_devOffset = 0;
	public static final int st_inoOffset = 12;
	public static final int st_modeOffset = 16;
	public static final int st_nlinkOffset = 20;
	public static final int st_uidOffset = 24;
	public static final int st_gidOffset = 28;
	public static final int st_rdevOffset = 32;
	public static final int st_sizeOffset = 44;
	public static final int st_blksizeOffset = 48;
	public static final int st_blocksOffset = 52;
	public static final int st_atimespecOffset = 56;
	public static final int st_mtimespecOffset = 64;
	public static final int st_ctimespecOffset = 72;
	
	public static final int st_bufsize = 88;
	
	static final int getBufferSize32()
	{
		return st_bufsize;
	}
	
	protected StatStructure32(byte [] buffer, int offset)
	{
		super(buffer, offset);
		if (buffer.length - offset < st_bufsize)
			throw new IllegalArgumentException("buffer length = " + buffer.length +", offset = "+offset+", st_bufsize = "+
					st_bufsize+ ", minimum expected length = "+offset + st_bufsize);
	}
	
	public long get_st_dev()
	{
		return getIntAtOffset(st_devOffset);
	}

	public long get_st_ino()
	{
		return (long)getIntAtOffset(st_inoOffset);
	}

	public int get_st_mode()
	{
		return getShortAtOffset(st_modeOffset);
	}

	public long get_st_nlink()
	{
		return getShortAtOffset(st_nlinkOffset);
	}

	public int get_st_uid()
	{
		return getIntAtOffset(st_uidOffset);
	}

	public int get_st_gid()
	{
		return getIntAtOffset(st_gidOffset);
	}

	public long get_st_rdev()
	{
		return getIntAtOffset(st_rdevOffset);
	}

    public long get_st_atime()
	{
		return getIntAtOffset(st_atimespecOffset);
	}

	public long get_st_atimensec()
	{
		return getIntAtOffset(st_atimespecOffset) + 4;
	}

	public long get_st_mtime()
	{
		return getIntAtOffset(st_modeOffset);
	}

	public long get_st_mtimensec()
	{
		return getIntAtOffset(st_mtimespecOffset) + 4;
	}

	public long get_st_ctime()
	{
		return getIntAtOffset(st_ctimespecOffset);
	}

	public long get_st_ctimensec()
	{
		return getIntAtOffset(st_ctimespecOffset + 4);
	}

	public long get_st_size()
	{
		return getIntAtOffset(st_sizeOffset);
	}

	public long get_st_blocks()
	{
		return getIntAtOffset(st_blocksOffset);
	}

	public long get_st_blksize()
	{
		return getIntAtOffset(st_blksizeOffset);
	}
}
