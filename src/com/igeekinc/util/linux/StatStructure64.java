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

public class StatStructure64 extends StatStructure
{
	public static final int st_devOffset = 0;
	public static final int st_inoOffset = 8;
	public static final int st_nlinkOffset = 16;
	public static final int st_modeOffset = 24;
	public static final int st_uidOffset = 28;
	public static final int st_gidOffset = st_uidOffset + 4;
	public static final int st_rdevOffset = 40;
	public static final int st_sizeOffset = 48;
	public static final int st_blksizeOffset = 56;
	public static final int st_blocksOffset = 64;
	public static final int st_atimespecOffset = 72;
	public static final int st_mtimespecOffset = 88;
	public static final int st_ctimespecOffset = 104;
	
	public static final int st_bufsize = 144;
	
	static final int getBufferSize64()
	{
		return st_bufsize;
	}
	protected StatStructure64(byte [] buffer, int offset)
	{
		super(buffer, offset);
		if (buffer.length - offset < st_bufsize)
			throw new IllegalArgumentException("buffer length = " + buffer.length +", offset = "+offset+", st_bufsize = "+
					st_bufsize+ ", minimum expected length = "+offset + st_bufsize);

	}
	
	public long get_st_dev()
	{
		return getLongAtOffset(st_devOffset);
	}

	public int get_st_mode()
	{
		return getIntAtOffset(st_modeOffset);
	}

	public long get_st_nlink()
	{
		return getLongAtOffset(st_nlinkOffset);
	}
	
	public long get_st_ino()
	{
		return getLongAtOffset(st_inoOffset);
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
		return getLongAtOffset(st_rdevOffset);
	}

    public long get_st_atime()
	{
		return getLongAtOffset(st_atimespecOffset);
	}

	public long get_st_atimensec()
	{
		return getLongAtOffset(st_atimespecOffset) + 8;
	}

	public long get_st_mtime()
	{
		return getLongAtOffset(st_mtimespecOffset);
	}

	public long get_st_mtimensec()
	{
		return getLongAtOffset(st_mtimespecOffset) + 8;
	}

	public long get_st_ctime()
	{
		return getLongAtOffset(st_ctimespecOffset);
	}

	public long get_st_ctimensec()
	{
		return getLongAtOffset(st_ctimespecOffset + 8);
	}

	public long get_st_size()
	{
		return getLongAtOffset(st_sizeOffset);
	}

	public long get_st_blocks()
	{
		return getLongAtOffset(st_blocksOffset);
	}

	public long get_st_blksize()
	{
		return getLongAtOffset(st_blksizeOffset);
	}
}
