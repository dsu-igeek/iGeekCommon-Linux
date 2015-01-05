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
import com.igeekinc.util.BufferStructure;
import com.igeekinc.util.SystemInfo;

public abstract class StatFSStructure extends BufferStructure
{	
	public StatFSStructure()
	{
		super(null, 0, 0);
	}
	
	public StatFSStructure(byte [] buffer, int offset)
	{
		super(buffer, offset, buffer.length - offset);
	}
	
	public static StatFSStructure getStatFSStructure(byte [] data, int offset)
	{
		if (SystemInfo.is32BitVM())
			return new StatFSStructure32(data, offset);
		else
			return new StatFSStructure64(data, offset);
	}
	
	public static int getBufferSize()
	{
		if (SystemInfo.is32BitVM())
			return StatFSStructure32.getBufferSize32();
		else
			return StatFSStructure64.getBufferSize64();
	}
	
	/**
	 * @return Returns the f_bavail.
	 */
	public abstract long getF_bavail();

	/**
	 * @return Returns the f_bfree.
	 */
	public abstract long getF_bfree();

	/**
	 * @return Returns the f_blocks.
	 */
	public abstract long getF_blocks();

	/**
	 * @return Returns the f_bsize.
	 */
	public abstract long getF_bsize();

	/**
	 * @return Returns the f_ffree.
	 */
	public abstract long getF_ffree();

	/**
	 * @return Returns the f_files.
	 */
	public abstract long getF_files();

	/**
	 * @return Returns the f_fsid.
	 */
	public abstract long getF_fsid();
}
