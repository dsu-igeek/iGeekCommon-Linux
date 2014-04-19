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

import com.sun.jna.Structure;

public class PasswordStructure extends Structure
{	
    public static class ByReference extends PasswordStructure implements Structure.ByReference {}
    public String pw_name;             // username
    public String pw_passwd;           // user password
    public int pw_uid;                 // user ID 
    public int pw_gid;                 // group ID
    public String pw_gecos;            // user information     
    public String pw_dir;              // home directory
    public String pw_shell;            // shell program
}
