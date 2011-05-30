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

import org.apache.myfaces.extensions.validator.ExtValInformation;
import org.apache.myfaces.extensions.validator.core.DefaultExtValCoreConfiguration;
import org.apache.myfaces.extensions.validator.core.ExtValCoreConfiguration;
import org.apache.myfaces.extensions.validator.core.factory.DefaultFactoryFinder;
import org.apache.myfaces.extensions.validator.core.factory.FactoryFinder;
import org.apache.myfaces.extensions.validator.core.factory.FactoryNames;
import org.apache.myfaces.extensions.validator.core.storage.DefaultStorageManagerFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * 
 * @author Rudy De Busscher
 * since v4
 *
 */
public class ExtValCoreConfigurationCustomStorageManagerFactoryClassNameTestCase extends
        ExtValCoreConfigurationTestCase
{


    public static class CustomDefaultStorageManagerFactory extends DefaultStorageManagerFactory
    {

    }

    public static class Custom2DefaultStorageManagerFactory extends DefaultStorageManagerFactory
    {

    }

    @Override
    protected void addInitializationParameters()
    {
        super.addInitializationParameters();
        if (needXmlParameters())
        {
            addInitParameter(ExtValInformation.WEBXML_PARAM_PREFIX + ".CUSTOM_STORAGE_MANAGER_FACTORY",
                    CustomDefaultStorageManagerFactory.class.getName());

        }
    }

    @Override
    protected ExtValCoreConfiguration getCustomExtValCoreConfiguration()
    {
        if (needCustomConfig())
        {

            return new DefaultExtValCoreConfiguration()
            {
                @Override
                public String customStorageManagerFactoryClassName()
                {

                    return Custom2DefaultStorageManagerFactory.class.getName();
                }
            };
        }
        else
        {
            return null;
        }
    }

    @Test
    public void testCustomStorageManagerFactoryClassNameDefault()
    {
        FactoryFinder factoryFinder = DefaultFactoryFinder.getInstance();
        Object factory = factoryFinder.getFactory(FactoryNames.STORAGE_MANAGER_FACTORY, Object.class);
        Assert.assertEquals(DefaultStorageManagerFactory.class.getName(), factory.getClass().getName());
    }

    @Test
    public void testCustomStorageManagerFactoryClassNameWebXml()
    {
        FactoryFinder factoryFinder = DefaultFactoryFinder.getInstance();
        Object factory = factoryFinder.getFactory(FactoryNames.STORAGE_MANAGER_FACTORY, Object.class);
        Assert.assertEquals(CustomDefaultStorageManagerFactory.class.getName(), factory.getClass().getName());
    }

    @Test
    public void testCustomStorageManagerFactoryClassNameCustomConfig()
    {
        FactoryFinder factoryFinder = DefaultFactoryFinder.getInstance();
        Object factory = factoryFinder.getFactory(FactoryNames.STORAGE_MANAGER_FACTORY, Object.class);
        Assert.assertEquals(Custom2DefaultStorageManagerFactory.class.getName(), factory.getClass().getName());
    }

}
