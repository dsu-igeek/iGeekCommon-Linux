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
 
#include <unistd.h>
#include <sys/time.h>
#include <sys/errno.h>
#include <sys/stat.h>
#include <sys/mount.h>
#include <pwd.h>
#include <sys/param.h>
#include <sys/vfs.h>

#include "LinuxNativeFileRoutines.h"


/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    getpwnam
 * Signature: (Ljava/lang/String;Lcom/igeekinc/util/macos/macosx/PasswordStructure;)I
 */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_getpwnam
  (JNIEnv * env, jobject this, jstring user, jobject passwordStructure)
{
    const char * userName = (*env)->GetStringUTFChars(env, user, JNI_FALSE);
    struct       passwd * passwdStruct;
    jint         retVal=0;
    jfieldID     fieldID;
    jclass       psClass = (*env)->GetObjectClass(env, passwordStructure);
    
    passwdStruct = getpwnam(userName);
    (*env)->ReleaseStringUTFChars(env, user, userName);
    if (passwdStruct == NULL) // Couldn't find user or error
    {
        retVal = -1;
        return(retVal);
    }
    
    fieldID = (*env)->GetFieldID(env, psClass, "pw_name", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_name));
    
    fieldID = (*env)->GetFieldID(env, psClass, "pw_passwd", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_passwd));
        
    fieldID = (*env)->GetFieldID(env, psClass, "pw_uid", "I");
    (*env)->SetIntField(env, passwordStructure, fieldID, 
        (jint)passwdStruct->pw_uid);

    fieldID = (*env)->GetFieldID(env, psClass, "pw_gid", "I");
    (*env)->SetIntField(env, passwordStructure, fieldID, 
        (jint)passwdStruct->pw_gid);
        
    fieldID = (*env)->GetFieldID(env, psClass, "pw_gecos", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_gecos));
        

    fieldID = (*env)->GetFieldID(env, psClass, "pw_dir", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_dir));
        

    fieldID = (*env)->GetFieldID(env, psClass, "pw_shell", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_shell));

    return(retVal);
}

/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    getpwuid
 * Signature: (ILcom/igeekinc/util/macos/macosx/PasswordStructure;)I
 */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_getpwuid
  (JNIEnv * env, jobject this, jint userID, jobject passwordStructure)
{
    struct       passwd * passwdStruct;
    jint         retVal=0;
    jfieldID     fieldID;
    jclass       psClass = (*env)->GetObjectClass(env, passwordStructure);
    
    passwdStruct = getpwuid((uid_t)userID);
    if (passwdStruct == NULL) // Couldn't find user or error
    {
        retVal = -1;
        return(retVal);
    }
    
    fieldID = (*env)->GetFieldID(env, psClass, "pw_name", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_name));
    
    fieldID = (*env)->GetFieldID(env, psClass, "pw_passwd", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_passwd));
        
    fieldID = (*env)->GetFieldID(env, psClass, "pw_uid", "I");
    (*env)->SetIntField(env, passwordStructure, fieldID, 
        (jint)passwdStruct->pw_uid);

    fieldID = (*env)->GetFieldID(env, psClass, "pw_gid", "I");
    (*env)->SetIntField(env, passwordStructure, fieldID, 
        (jint)passwdStruct->pw_gid);
        
    fieldID = (*env)->GetFieldID(env, psClass, "pw_gecos", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_gecos));
        

    fieldID = (*env)->GetFieldID(env, psClass, "pw_dir", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_dir));
        

    fieldID = (*env)->GetFieldID(env, psClass, "pw_shell", "Ljava/lang/String;");
    (*env)->SetObjectField(env, passwordStructure, fieldID, 
        (*env)->NewStringUTF(env, passwdStruct->pw_shell));

    return(retVal);
}
/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    lstat
 * Signature: (Ljava/lang/String;[B)I
 */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_lstat
  (JNIEnv * env, jobject this, jstring file, jbyteArray bufArray)
{
    const char * fileName = (*env)->GetStringUTFChars(env, file, JNI_FALSE);
    
	struct stat retrieveBuf;
	int bufLen = 0;
	jint result = 0;
	if (bufArray == NULL)
	{
		result = EINVAL;
	}
	else
	{
		bufLen = (*env)->GetArrayLength(env, bufArray);
		

		if (bufLen < sizeof(struct stat))
		{
			fprintf(stderr, "lstat called with buffer too small - expected %d\n", sizeof(struct stat));
			result = EINVAL;
		}
		else
		{
			result = lstat(fileName, &retrieveBuf);
			if (result == 0)
				(*env)->SetByteArrayRegion(env, bufArray, 0, sizeof(struct stat), (const jbyte *)&retrieveBuf);
			else
				result = errno;
		}
	}
	(*env)->ReleaseStringUTFChars(env, file, fileName);

	return result;
}

/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    link
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_link
  (JNIEnv * env, jobject this, jstring existingStr, jstring newStr)
{
    const char * existing = (*env)->GetStringUTFChars(env, existingStr, JNI_FALSE);
    const char * new = (*env)->GetStringUTFChars(env, newStr, JNI_FALSE);
    jint result = link(existing, new);
    if (result != 0)
    {
        result = errno;
    }
 
    (*env)->ReleaseStringUTFChars(env, existingStr, existing);
    (*env)->ReleaseStringUTFChars(env, newStr, new);
    return(result);
}

/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    symlink
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_symlink
  (JNIEnv * env, jobject this, jstring existingStr, jstring newStr)
{
    const char * existing = (*env)->GetStringUTFChars(env, existingStr, JNI_FALSE);
    const char * new = (*env)->GetStringUTFChars(env, newStr, JNI_FALSE);
    jint result = symlink(existing, new);
    if (result != 0)
        result = errno;
    (*env)->ReleaseStringUTFChars(env, existingStr, existing);
    (*env)->ReleaseStringUTFChars(env, newStr, new);
    return(result);

}

/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    readlink
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_readlink
  (JNIEnv * env, jobject this, jstring file)
{
    char pathBuf[MAXPATHLEN];
    const char * path = (*env)->GetStringUTFChars(env, file, JNI_FALSE);
    int result = readlink(path, pathBuf, MAXPATHLEN);
    jstring returnStr = NULL;
    if (result > 0)
    {
        pathBuf[result] = 0;
        returnStr = (*env)->NewStringUTF(env, pathBuf);
    }
    return(returnStr);
}

/*  * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    utimes
 * Signature: (Ljava/lang/String;IIII)I */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_utimes
  (JNIEnv * env, jobject this, jstring filePathString, jint accessSecs, jint accessUSecs, jint modifySecs, jint modifyUSecs)
{
    const char * filePath = (*env)->GetStringUTFChars(env, filePathString, JNI_FALSE);
    struct timeval newTimes[2];
    int retVal;
    newTimes[0].tv_sec = accessSecs;
    newTimes[0].tv_usec = accessUSecs;
    newTimes[1].tv_sec = modifySecs;
    newTimes[1].tv_usec = modifyUSecs;    
    retVal = utimes(filePath, newTimes);
    (*env)->ReleaseStringUTFChars(env, filePathString, filePath);
    return(retVal);
}  

/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    rename
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_rename
  (JNIEnv * env, jobject this, jstring srcStr, jstring destStr)
{
    const char * src = (*env)->GetStringUTFChars(env, srcStr, JNI_FALSE);
    const char * dest = (*env)->GetStringUTFChars(env, destStr, JNI_FALSE);
	/*
    jint result = link(src, dest);
 
    if (result != 0)
    {
        result = errno;
    }
    else
    {
    	result = unlink(src);
    	if (result != 0)
    		result = errno;
    }
    */
    jint result = rename(src, dest);
    if (result != 0)
    	result = errno;
    (*env)->ReleaseStringUTFChars(env, srcStr, src);
    (*env)->ReleaseStringUTFChars(env, destStr, dest);
    return(result);

}

JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_statfs
  (JNIEnv * env, jobject this, jstring volumePathString, jarray bufArray)
{
    const char * volumePath = (*env)->GetStringUTFChars(env, volumePathString, JNI_FALSE);    
    
	jbyte * buf;
	jboolean isCopy;
	int bufLen = 0;
	if (bufArray == NULL)
		return(-1);
	buf = (*env)->GetByteArrayElements(env, bufArray, &isCopy);
	bufLen = (*env)->GetArrayLength(env, bufArray);
	
	if (bufLen < sizeof(struct statfs))
	{
		fprintf(stderr, "statfs called with buffer too small, expected %d, got %d\n", sizeof(struct statfs), bufLen);
		return(-1);
	}
	
	jint retVal = statfs(volumePath, (struct statfs *)buf);
	if (retVal != 0)
        retVal = errno;

	(*env)->ReleaseByteArrayElements(env, bufArray, buf, 0);
	(*env)->ReleaseStringUTFChars(env, volumePathString, volumePath);
	return(retVal);
	return 0;
}

/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    chmod
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_chmod
  (JNIEnv * env, jobject this, jstring filePathString, jint mode)
{
    const char * filePath = (*env)->GetStringUTFChars(env, filePathString, JNI_FALSE);
	int retVal;
	
	retVal = chmod(filePath, mode);
	
    (*env)->ReleaseStringUTFChars(env, filePathString, filePath);
    return(retVal);
}  

/*
 * Class:     com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines
 * Method:    chown
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_chown
  (JNIEnv * env, jobject this, jstring filePathString, jint uid, jint gid)
{
    const char * filePath = (*env)->GetStringUTFChars(env, filePathString, JNI_FALSE);
	int retVal;
	
	retVal = chown(filePath, uid, gid);
    (*env)->ReleaseStringUTFChars(env, filePathString, filePath);
    return(retVal);
}

JNIEXPORT jint JNICALL Java_com_igeekinc_util_linux_nativeifs_LinuxNativeFileRoutines_mount
  (JNIEnv * env, jobject this, jstring sourceString, jstring mountDirString,
	jstring filesystemtypeString,
  	jint flags, jarray dataArray)
{
	jbyte * data;
	jboolean isCopy;

	const char * source = (*env)->GetStringUTFChars(env, sourceString, JNI_FALSE);
	const char * mountDir = (*env)->GetStringUTFChars(env, mountDirString, JNI_FALSE);
	const char * filesystemtype = (*env)->GetStringUTFChars(env, filesystemtypeString, JNI_FALSE);
	
	int bufLen = 0;
	
	if (dataArray != NULL)
	{
		data = (*env)->GetByteArrayElements(env, dataArray, &isCopy);
		bufLen = (*env)->GetArrayLength(env,dataArray);
	}
	else
		data = NULL;	
	int retVal = mount(source, mountDir, filesystemtype, flags, data);
	if (retVal != 0)
		retVal = errno;
	if (dataArray != NULL)
		(*env)->ReleaseByteArrayElements(env, dataArray, data, 0);
	(*env)->ReleaseStringUTFChars(env, sourceString, source);
	(*env)->ReleaseStringUTFChars(env, mountDirString, mountDir);
	(*env)->ReleaseStringUTFChars(env, filesystemtypeString, filesystemtype);
	return(retVal);
}
