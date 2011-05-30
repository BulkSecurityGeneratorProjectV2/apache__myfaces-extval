/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.extensions.validator.test.trinidad.initializer;

import org.apache.myfaces.extensions.validator.baseval.annotation.Length;
import org.apache.myfaces.extensions.validator.baseval.annotation.Required;
import org.apache.myfaces.extensions.validator.crossval.annotation.RequiredIf;

import javax.persistence.Id;

/**
 * @author Rudy De Busscher
 */
public class DataBean
{
    @Id
    @Length(maximum = 20)
    private String property1;

    @RequiredIf(valueOf = "property1")
    private String property2;

    @Required
    private String property3;

    private String property4;

    public String getProperty1()
    {
        return property1;
    }

    public void setProperty1(String property1)
    {
        this.property1 = property1;
    }

    public String getProperty2()
    {
        return property2;
    }

    public void setProperty2(String property2)
    {
        this.property2 = property2;
    }

    public String getProperty3()
    {
        return property3;
    }

    public void setProperty3(String property3)
    {
        this.property3 = property3;
    }

    public String getProperty4()
    {
        return property4;
    }

    public void setProperty4(String property4)
    {
        this.property4 = property4;
    }

}
