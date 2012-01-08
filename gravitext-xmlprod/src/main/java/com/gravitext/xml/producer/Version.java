/*
 * Copyright (c) 2008-2012 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gravitext.xml.producer;

/**
 * XML versions supported.
 *
 * @author David Kellum
 */
public enum Version
{
    /**
     * XML 1.0
     */
    XML_1_0 { public String toString() { return "1.0"; } },

    /**
     * XML 1.1
     */
    XML_1_1 { public String toString() { return "1.1"; } }
}
