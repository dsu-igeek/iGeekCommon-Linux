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
 
package com.igeekinc.util.linux.unixerrors;

public class TooManyLevelsOfSymlinkErrorException extends GeneralFileErrorException
{
    private static final long serialVersionUID = -1462785801641602652L;

    public TooManyLevelsOfSymlinkErrorException(String message, String filePath)
    {
        super(ELOOP, (message == null || message.length()==0)?"Too many levels of symbolic links encountered accessing "+filePath:message, filePath);
    }
}
