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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import com.igeekinc.util.ClientFile;
import com.igeekinc.util.ClientFileMetaData;
import com.igeekinc.util.FileCopy;
import com.igeekinc.util.FileCopyProgressIndicator;
import com.igeekinc.util.FileCopyProgressIndicatorIF;
import com.igeekinc.util.FilePackage;
import com.igeekinc.util.datadescriptor.DataDescriptor;
import com.igeekinc.util.formats.splitfile.SplitFileDescriptor;
import com.igeekinc.util.pauseabort.AbortedException;
import com.igeekinc.util.pauseabort.PauserControlleeIF;

public class LinuxFileCopy extends FileCopy
{
	final static int kGzipType = 0x477a6970;		// 'Gzip
	final static int kStuffitCreator = 0x53495478;	// 'SITx
	/**
	 * 
	 */
	public LinuxFileCopy()
	{

	}
	public void copyFile(
		ClientFile source,
		boolean decompressSource,
		ClientFile destination,
		boolean compressDestination,
		boolean copyMetaData,
		PauserControlleeIF pauser,
		FileCopyProgressIndicator progress)
		throws IOException, AbortedException
	{

			copyToFile((LinuxClientFile)source, decompressSource, (LinuxClientFile)destination, compressDestination, copyMetaData,
					pauser, progress);
	}
	

	/**
	 * Used for copying non-HFS files to either a non-HFS volume or an HFS volume
	 * @param source
	 * @param decompressSource
	 * @param destination
	 * @param compressDestination
	 * @param copyMetaData
	 * @param pauser
	 * @param progress
	 * @throws IOException
	 * @throws AbortedException
	 */
	public void copyToFile(
			LinuxClientFile source,
			boolean decompressSource,
			LinuxClientFile destination,
			boolean compressDestination,
			boolean copyMetaData,
			PauserControlleeIF pauser,
			FileCopyProgressIndicator progress)
	throws IOException, AbortedException
	{
		InputStream sourceStream;
		OutputStream destStream;
		pauser.checkPauseAndAbort();
		boolean sourceCompressed = false, sourceAppleSingleFormat = false;
		if (source.getName().endsWith(".gz"))
			sourceCompressed = true;
		if (source.getName().endsWith(".as.gz"))
			sourceAppleSingleFormat = true;
		LinuxFileMetaData sourceMD =
			(LinuxFileMetaData)source.getMetaData();
		switch (sourceMD.getFileType())
		{
			case LinuxFileMetaData.kSymbolicLinkType :

				destination.mkSymlink(sourceMD.getSymlinkTarget());
				break;
			case LinuxFileMetaData.kRegularFileType:
				pauser.checkPauseAndAbort();
				if (!sourceCompressed && !compressDestination)
				{
					sourceStream = new FileInputStream(source);
					destStream = new FileOutputStream(destination);
					copyFork(sourceStream, destStream, pauser, progress);
					sourceStream.close();
					destStream.close();
					
					pauser.checkPauseAndAbort();
				}
/*
				if (sourceCompressed && decompressSource)
				{
					if (!source.getName().endsWith(".as.gz"))
						throw new IllegalArgumentException("Compressed file source names should end with .as.gz");
					sourceStream = new GZIPInputStream(new FileInputStream(source));
					AppleSingleFormat appleSingleFormat = new AppleSingleFormat(sourceStream);
					destStream = new FileOutputStream(destination);
					ResourceForkOutputStream rsrcDestStream = new ResourceForkOutputStream(destination);
					appleSingleFormat.streamData(destStream, rsrcDestStream, pauser, progress);
					destStream.close();
					rsrcDestStream.close();
					sourceMD.setFromAppleSingleFormat(appleSingleFormat); // Fill in type and creator stuff from the AppleSingle info
				}

				if (compressDestination)
				{
					if (!destination.getName().endsWith(".as.gz"))
						throw new IllegalArgumentException("Compressed file destination names should end with .az.gz");


					AppleSingleFormat destinationAS =
						new AppleSingleFormat(destination);

					byte [] fileNameBytes = source.getName().getBytes();
					DataSource fileNameSource = new ByteArrayDataSource(fileNameBytes);
					destinationAS.createEntry(
						AppleSingleEntry.kRealName,
						fileNameBytes.length,
						fileNameSource);
					sourceMD.outputToAppleSingleFormat(destinationAS);
					DataSource data = new FileDataSource(source);
					destinationAS.createEntry(
						AppleSingleEntry.kDataForkID,
						(int) sourceMD.getDataLength(),
						data);
					DataSource resource = new ResourceForkDataSource(source);
					destinationAS.createEntry(
						AppleSingleEntry.kResourceForkID,
						(int) sourceMD.getResourceLength(),
						resource);
					destStream = new FileOutputStream(destination);
					destStream = new GZIPOutputStream(destStream);
					destinationAS.writeEntries(destStream, pauser, progress);
					destStream.close();
				}
*/
				LinuxFileMetaData destinationMD =
					(LinuxFileMetaData) destination.getMetaData();
				destinationMD.setFromMetaData(sourceMD);
				destination.setMetaData(destinationMD);

				break;

			default:
		}
	}

	/* (non-Javadoc)
	 * @see com.igeekinc.util.FileCopy#encryptFile(com.igeekinc.util.ClientFile, com.igeekinc.util.ClientFile, java.security.PublicKey, java.security.Key, com.igeekinc.util.PauseAbort, com.igeekinc.util.FileCopyProgressIndicator)
	 */
	public void encryptFile(
		ClientFile source,
		ClientFile destination,
		PublicKey publicKey,
		Key fileKey,
		boolean compress,
		boolean copyMetaData,
		PauserControlleeIF pauser,
		FileCopyProgressIndicator progress)
		throws IOException, AbortedException
	{

	}
    public void decryptFile(ClientFile source, boolean decompressSource,
            PrivateKey unlockKey, ClientFile destination, boolean copyMetaDate,
            PauserControlleeIF pauser, FileCopyProgressIndicator progress)
            throws IOException, AbortedException
    {
        // TODO Auto-generated method stub

    }
	@Override
	public void copyFile(ClientFile source, boolean decompressSource,
			ClientFile destination, boolean compressDestination,
			boolean copyMetaData, PauserControlleeIF pauser,
			FileCopyProgressIndicatorIF progress) throws IOException,
			AbortedException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void copyFile(ClientFile source, boolean decompressSource,
			SplitFileDescriptor destination, boolean compressDestination,
			boolean copyMetaData, PauserControlleeIF pauser,
			FileCopyProgressIndicatorIF progress) throws IOException,
			AbortedException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void copyFile(SplitFileDescriptor source, boolean decompressSource,
			ClientFile destination, boolean compressDestination,
			boolean copyMetaData, PauserControlleeIF pauser,
			FileCopyProgressIndicatorIF progress) throws IOException,
			AbortedException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void decryptFile(ClientFile source, boolean decompressSource,
			PrivateKey unlockKey, ClientFile destination, boolean copyMetaDate,
			PauserControlleeIF pauser, FileCopyProgressIndicatorIF progress)
			throws IOException, AbortedException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void decryptFile(SplitFileDescriptor source,
			boolean decompressSource, PrivateKey unlockKey,
			ClientFile destination, boolean copyMetaDate,
			PauserControlleeIF pauser, FileCopyProgressIndicatorIF progress)
			throws IOException, AbortedException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void encryptFile(ClientFile source, ClientFile destination,
			PublicKey publicKey, Key fileKey, boolean compressDestination,
			boolean copyMetaData, PauserControlleeIF pauser,
			FileCopyProgressIndicatorIF progress) throws IOException,
			AbortedException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void encryptFile(ClientFile source, SplitFileDescriptor destination,
			PublicKey publicKey, Key fileKey, boolean compressDestination,
			boolean copyMetaData, PauserControlleeIF pauser,
			FileCopyProgressIndicatorIF progress) throws IOException,
			AbortedException {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void encryptFile(String fileName,
            HashMap<String, ? extends DataDescriptor> forkData,
            HashMap<String, ? extends DataDescriptor> extendedAttributeData,
            ClientFileMetaData sourceMetaData, ClientFile destination,
            PublicKey publicKey, Key fileKey, boolean compressDestination,
            boolean copyMetaData, PauserControlleeIF pauser,
            FileCopyProgressIndicatorIF progress) throws IOException,
            AbortedException
    {
        // TODO Auto-generated method stub
        
    }
	@Override
	public void copyFile(FilePackage filePackage, boolean decompressSource, ClientFile destination,
			boolean compressDestination, boolean copyMetaData,
			PauserControlleeIF pauser, FileCopyProgressIndicatorIF progress)
			throws IOException, AbortedException
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void encryptFile(String fileName, FilePackage filePackage,
			ClientFile destination, PublicKey publicKey, Key fileKey,
			boolean compressDestination, boolean copyMetaData,
			PauserControlleeIF pauser, FileCopyProgressIndicatorIF progress)
			throws IOException, AbortedException
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void decryptFile(FilePackage source, boolean decompressSource,
			PrivateKey unlockKey, ClientFile destination, boolean copyMetaDate,
			PauserControlleeIF pauser, FileCopyProgressIndicatorIF progress)
			throws IOException, AbortedException
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void copyFile(FilePackage filePackage, boolean decompressSource,
			SplitFileDescriptor destination, boolean compressDestination,
			boolean copyMetaData, PauserControlleeIF pauser,
			FileCopyProgressIndicatorIF progress) throws IOException,
			AbortedException
	{
		// TODO Auto-generated method stub
		
	}
}
