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

import com.igeekinc.util.BitTwiddle;
import com.igeekinc.util.SystemInfo;

public class StatFSStructure
{
	
	public static final int  f_typeOffset = 0;
	public static final int  f_bsizeOffset = f_typeOffset+SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  f_blocksOffset = f_bsizeOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  f_bfreeOffset = f_blocksOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  f_bavailOffset = f_bfreeOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  f_filesOffset = f_bavailOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  f_ffreeOffset = f_filesOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  f_fsidOffset = f_ffreeOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int	 f_namelenOffset = f_fsidOffset + (SystemInfo.getSystemInfo().getNativeLongSize() * 2);
	public static final int  f_frsizeOffset = f_namelenOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  f_flagsOffset = f_frsizeOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  f_spareOffset = f_flagsOffset + SystemInfo.getSystemInfo().getNativeLongSize();
	public static final int  fsstatBufSize = f_spareOffset + (SystemInfo.getSystemInfo().getNativeLongSize()*4) /*reserved space */;
	
	public int    f_type;    /* type of file system (reserved: zero) */
	public int    f_bsize;    /* fundamental file system block size */
	public int    f_blocks;   /* total data blocks in file system */
	public int    f_bfree;    /* free blocks in fs */
	public int    f_bavail;   /* free blocks avail to non-superuser */
	public int    f_files;    /* total file nodes in file system */
	public int    f_ffree;    /* free file nodes in fs */
	public int    f_fsid;     /* file system id (super-user only) */
	public int    f_namelen;
	
	public StatFSStructure()
	{
		
	}
	public StatFSStructure(byte [] data, int offset)
	{
		f_type = BitTwiddle.nativeByteArrayToInt(data, offset + f_typeOffset);
		f_bsize = BitTwiddle.nativeByteArrayToInt(data, offset + f_bsizeOffset);
		f_blocks = BitTwiddle.nativeByteArrayToInt(data, offset + f_blocksOffset);
		f_bfree = BitTwiddle.nativeByteArrayToInt(data, offset + f_bfreeOffset);
		f_bavail = BitTwiddle.nativeByteArrayToInt(data, offset + f_bavailOffset);
		f_files = BitTwiddle.nativeByteArrayToInt(data, offset + f_filesOffset);
		f_fsid = BitTwiddle.nativeByteArrayToInt(data, offset + f_fsidOffset);
		f_ffree = BitTwiddle.nativeByteArrayToInt(data, offset + f_ffreeOffset);
		f_namelen = BitTwiddle.nativeByteArrayToInt(data, offset + f_namelenOffset);
	}
	/**
	 * @return Returns the fsstatBufSize.
	 */
	public static int getFsstatBufSize()
	{
		return fsstatBufSize;
	}

	/**
	 * @return Returns the f_bavail.
	 */
	public long getF_bavail()
	{
		return f_bavail;
	}

	/**
	 * @return Returns the f_bfree.
	 */
	public long getF_bfree()
	{
		return f_bfree;
	}

	/**
	 * @return Returns the f_blocks.
	 */
	public long getF_blocks()
	{
		return f_blocks;
	}

	/**
	 * @return Returns the f_bsize.
	 */
	public long getF_bsize()
	{
		return f_bsize;
	}

	/**
	 * @return Returns the f_ffree.
	 */
	public long getF_ffree()
	{
		return f_ffree;
	}

	/**
	 * @return Returns the f_files.
	 */
	public long getF_files()
	{
		return f_files;
	}

	/**
	 * @return Returns the f_fsid.
	 */
	public long getF_fsid()
	{
		return f_fsid;
	}
}
