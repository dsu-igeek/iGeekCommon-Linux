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
 
#include <stdio.h>
#include <sys/stat.h>
#include <sys/types.h>
int main (int argc, const char * argv[]) 
{
	struct stat test;
	
	void * start = &test;
	
#if __WORDSIZE == 64
	printf("64 bit mode\n");

	printf("__WORDSIZE==64\n");
	printf("sizeof struct stat = %ld\n", sizeof(test));
	printf("test start ptr = %lx\n", (void *)start);
	printf("st_dev offset = 0, st_dev size = %ld\n", sizeof(test.st_dev));
	printf("st_ino offset = %ld, st_ino size = %ld\n", (char *)&test.st_ino - (char *)&test, sizeof(test.st_ino));
	printf("st_nlink offset = %ld, st_nlink size = %ld\n", (char *)&test.st_nlink - (char *)&test, sizeof(test.st_nlink));
	printf("st_mode offset = %ld, st_mode size = %ld\n", (char *)&test.st_mode - (char *)&test, sizeof(test.st_mode));
	

	printf("st_uid offset = %ld, st_uid size = %ld\n", (char *)&test.st_uid - (char *)&test, sizeof(test.st_uid));
	printf("st_gid offset = %ld, st_gid size = %ld\n", (char *)&test.st_gid - (char *)&test, sizeof(test.st_gid));
	
	printf("st_rdev offset = %ld, st_rdev size = %ld\n", (char *)&test.st_rdev - (char *)&test, sizeof(test.st_rdev));
	printf("st_size offset = %ld, st_size size = %ld\n", (char *)&test.st_size - (char *)&test, sizeof(test.st_size));
	printf("st_blksize offset = %ld, st_blksize size = %ld\n", (char *)&test.st_blksize - (char *)&test, sizeof(test.st_blksize));
	printf("st_blocks offset = %ld, st_blocks size = %ld\n", (char *)&test.st_blocks - (char *)&test, sizeof(test.st_blocks));
	
	printf("st_atimespec offset = %ld, st_atimespec size = %ld\n", (char *)&test.st_atim - (char *)&test, sizeof(test.st_atim));
	printf("st_mtimespec offset = %ld, st_mtimespec size = %ld\n", (char *)&test.st_mtim - (char *)&test, sizeof(test.st_mtim));
	printf("st_ctimespec offset = %ld, st_ctimespec size = %ld\n", (char *)&test.st_ctime - (char *)&test, sizeof(test.st_ctim));

/* -- Figure out if Linux has a legacy mode --
	printf("old style 64 bit\n");
	printf("sizeof struct stat = %ld\n", sizeof(test));
	printf("test start ptr = %lx\n", (uintptr_t)start);
	printf("st_dev offset = 0, st_dev size = %ld\n", sizeof(test.st_dev));
	printf("st_ino offset = %ld, st_ino size = %ld\n", (char *)&test.st_ino - (char *)&test, sizeof(test.st_ino));
	printf("st_mode offset = %ld, st_mode size = %ld\n", (char *)&test.st_mode - (char *)&test, sizeof(test.st_mode));
	printf("st_nlink offset = %ld, st_nlink size = %ld\n", (char *)&test.st_nlink - (char *)&test, sizeof(test.st_nlink));
	printf("st_uid offset = %ld, st_uid size = %ld\n", (char *)&test.st_uid - (char *)&test, sizeof(test.st_uid));
	printf("st_gid offset = %ld, st_gid size = %ld\n", (char *)&test.st_gid - (char *)&test, sizeof(test.st_gid));
	printf("st_rdev offset = %ld, st_rdev size = %ld\n", (char *)&test.st_rdev - (char *)&test, sizeof(test.st_rdev));
	printf("st_atimespec offset = %ld, st_atimespec size = %ld\n", (char *)&test.st_atimespec - (char *)&test, sizeof(test.st_atimespec));
	printf("st_mtimespec offset = %ld, st_mtimespec size = %ld\n", (char *)&test.st_mtimespec - (char *)&test, sizeof(test.st_mtimespec));
	printf("st_ctimespec offset = %ld, st_ctimespec size = %ld\n", (char *)&test.st_ctimespec - (char *)&test, sizeof(test.st_ctimespec));
	printf("st_size offset = %ld, st_size size = %ld\n", (char *)&test.st_size - (char *)&test, sizeof(test.st_size));
	printf("st_blocks offset = %ld, st_blocks size = %ld\n", (char *)&test.st_blocks - (char *)&test, sizeof(test.st_blocks));
	printf("st_blksize offset = %ld, st_blksize size = %ld\n", (char *)&test.st_blksize - (char *)&test, sizeof(test.st_blksize));
	printf("st_flags offset = %ld, st_flags size = %ld\n", (char *)&test.st_flags - (char *)&test, sizeof(test.st_flags));
	printf("st_gen offset = %ld, st_gen size = %ld\n", (char *)&test.st_gen - (char *)&test, sizeof(test.st_gen));
	printf("st_lspare offset = %ld, st_lspare size = %ld\n", (char *)&test.st_lspare - (char *)&test, sizeof(test.st_lspare));
	printf("st_qspare offset = %ld, st_qspare size = %ld\n", (char *)&test.st_qspare - (char *)&test, sizeof(test.st_qspare));
*/

#else
	printf("32 bit mode\n");
	printf("sizeof struct stat = %d\n", sizeof(test));
	printf("test start ptr = %x\n", (void *)start);
	printf("st_dev offset = 0, st_dev size = %d\n", sizeof(test.st_dev));
	printf("st_ino offset = %d, st_ino size = %d\n", (char *)&test.st_ino - (char *)&test, sizeof(test.st_ino));
	printf("st_mode offset = %d, st_mode size = %d\n", (char *)&test.st_mode - (char *)&test, sizeof(test.st_mode));
	printf("st_nlink offset = %d, st_nlink size = %d\n", (char *)&test.st_nlink - (char *)&test, sizeof(test.st_nlink));
	printf("st_uid offset = %d, st_uid size = %d\n", (char *)&test.st_uid - (char *)&test, sizeof(test.st_uid));
	printf("st_gid offset = %d, st_gid size = %d\n", (char *)&test.st_gid - (char *)&test, sizeof(test.st_gid));
	printf("st_rdev offset = %d, st_rdev size = %d\n", (char *)&test.st_rdev - (char *)&test, sizeof(test.st_rdev));
	printf("st_atimespec offset = %d, st_atimespec size = %d\n", (char *)&test.st_atim - (char *)&test, sizeof(test.st_atim));
	printf("st_mtimespec offset = %d, st_mtimespec size = %d\n", (char *)&test.st_mtim- (char *)&test, sizeof(test.st_mtim));
	printf("st_ctimespec offset = %d, st_ctimespec size = %d\n", (char *)&test.st_ctim- (char *)&test, sizeof(test.st_ctim));
	printf("st_size offset = %d, st_size size = %d\n", (char *)&test.st_size - (char *)&test, sizeof(test.st_size));
	printf("st_blocks offset = %d, st_blocks size = %d\n", (char *)&test.st_blocks - (char *)&test, sizeof(test.st_blocks));
	printf("st_blksize offset = %d, st_blksize size = %d\n", (char *)&test.st_blksize - (char *)&test, sizeof(test.st_blksize));
	
#endif
}
