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

import java.io.File;
import java.io.Serializable;

import com.igeekinc.util.User;

public class LinuxUser extends User
    implements Serializable
{
  static final long serialVersionUID = -5195180958018451118L;
  private int uid, gid;
  private String password;
  private File homeDir;

  
  public LinuxUser(PasswordStructure inInfo)
  {
    uid = inInfo.pw_uid;
    gid = inInfo.pw_gid;
    userName = inInfo.pw_name;
    longName = inInfo.pw_gecos;
    homeDir = new File(inInfo.pw_dir);
    password = inInfo.pw_passwd;
  }

  public String toString()
  {
    return(Integer.toString(uid));
  }

  public boolean equals(Object checkObject)
  {
    if (checkObject.getClass() != LinuxUser.class)
      return false;
    LinuxUser checkUser = (LinuxUser)checkObject;
    if (checkUser.uid == uid)
      return true;
    return false;
  }
	/* (non-Javadoc)
	 * @see com.igeekinc.util.User#getHomeDirectory()
	 */
	public File getHomeDirectory()
	{
		return homeDir;
	}
	
	public int getUID()
	{
		return uid;
	}
	
	public int getGID()
	{
		return gid;
	}

	public String getPasswordHash()
	{
		return password;
	}
}