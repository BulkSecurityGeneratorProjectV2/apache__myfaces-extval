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
package org.apache.myfaces.extensions.validator.test.core.config;

import org.apache.myfaces.extensions.validator.test.core.AbstractExValCoreTestCase;
import org.apache.myfaces.test.runners.NamedRunner;
import org.apache.myfaces.test.runners.TestPerClassLoaderRunner;
import org.junit.runner.RunWith;

/**
 * 
 * since v4
 *
 */
@RunWith(value = TestPerClassLoaderRunner.class)
public abstract class ExtValCoreConfigurationTestCase extends
        AbstractExValCoreTestCase
{
    protected String getName() {
        return NamedRunner.getTestMethodName();
    }

    protected boolean needXmlParameters()
    {
        return getName().contains("Xml");
    }

    protected boolean needCustomConfig()
    {
        return !getName().contains("Xml") && !getName().contains("Default");
    }

    @Override
    /*
     * Made the method final because we do
     */
    protected final void invokeStartupListeners()
    {
        super.invokeStartupListeners();
    }
    
}
