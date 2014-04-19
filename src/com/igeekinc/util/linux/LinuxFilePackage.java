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

import java.io.IOException;
import java.util.HashMap;

import com.igeekinc.util.ClientFile;
import com.igeekinc.util.ClientFileMetaData;
import com.igeekinc.util.FilePackage;
import com.igeekinc.util.FilePath;
import com.igeekinc.util.datadescriptor.DataDescriptor;
import com.igeekinc.util.exceptions.ForkNotFoundException;

public class LinuxFilePackage extends FilePackage
{
	private static final long	serialVersionUID	= 7221696275194887405L;

	public LinuxFilePackage(FilePath filePath,
			HashMap<String, ? extends DataDescriptor> forkData,
			HashMap<String, ? extends DataDescriptor> extendedAttributeData,
			ClientFileMetaData metaData)
	{
		super(filePath, forkData, extendedAttributeData, metaData);
	}

	public LinuxFilePackage(FilePath filePath, ClientFile sourceFile)
			throws ForkNotFoundException, IOException
	{
		super(filePath, sourceFile);
	}

}
