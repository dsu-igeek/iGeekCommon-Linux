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
#include <sys/types.h>
#include <sys/statfs.h>

int main (int argc, const char * argv[]) 
{
	struct statfs test;
	
	void * start = &test;
	
#if __WORDSIZE == 64
	printf("64 bit mode\n");

	printf("__WORDSIZE==64\n");
#endif
#if __WORDSIZE == 32
	printf("64 bit mode\n");

	printf("__WORDSIZE==32\n");
#endif
	printf("sizeof struct stat = %ld\n", sizeof(test));
	printf("test start ptr = %lx\n", (void *)start);
	printf("f_type offset = %ld, f_type size = %ld\n", (char *)&test.f_type - (char *)&test, sizeof(test.f_type));
	printf("f_bsize offset = %ld, f_bsize size = %ld\n", (char *)&test.f_bsize - (char *)&test, sizeof(test.f_type));
	printf("f_blocks offset = %ld, f_blocks size = %ld\n", (char *)&test.f_blocks - (char *)&test, sizeof(test.f_type));
	printf("f_bfree offset = %ld, f_bfree size = %ld\n", (char *)&test.f_bfree - (char *)&test, sizeof(test.f_bfree));
	printf("f_bavail offset = %ld, f_bavail size = %ld\n", (char *)&test.f_bavail - (char *)&test, sizeof(test.f_bavail));
	printf("f_files offset = %ld, f_files size = %ld\n", (char *)&test.f_files - (char *)&test, sizeof(test.f_files));
	printf("f_ffree offset = %ld, f_ffree size = %ld\n", (char *)&test.f_ffree - (char *)&test, sizeof(test.f_ffree));
	printf("f_fsid offset = %ld, f_fsid size = %ld\n", (char *)&test.f_fsid - (char *)&test, sizeof(test.f_fsid));
	printf("f_namelen offset = %ld, f_namelen size = %ld\n", (char *)&test.f_namelen - (char *)&test, sizeof(test.f_namelen));
	printf("f_frsize offset = %ld, f_frsize size = %ld\n", (char *)&test.f_frsize - (char *)&test, sizeof(test.f_frsize));
	printf("f_spare offset = %ld, f_spare size = %ld\n", (char *)&test.f_spare - (char *)&test, sizeof(test.f_spare));


}