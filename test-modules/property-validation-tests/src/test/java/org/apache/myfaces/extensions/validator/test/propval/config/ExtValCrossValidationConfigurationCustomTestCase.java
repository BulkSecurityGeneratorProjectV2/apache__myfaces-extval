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
package org.apache.myfaces.extensions.validator.test.propval.config;

import org.apache.myfaces.extensions.validator.crossval.DefaultExtValCrossValidationModuleConfiguration;
import org.apache.myfaces.extensions.validator.crossval.ExtValCrossValidationModuleConfiguration;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * since v4
 *
 */
public class ExtValCrossValidationConfigurationCustomTestCase extends ExtValCrossValidationConfigurationTestCase
{

    public static class CustomExtValCrossValidationModuleConfiguration extends
            DefaultExtValCrossValidationModuleConfiguration
    {

        @Override
        public boolean deactivateCrossvalidation()
        {
            return true;
        }


    }

    protected void addInitializationParameters()
    {
        super.addInitializationParameters();
        if (needCustomConfig())
        {
            addInitParameter(ExtValCrossValidationModuleConfiguration.class.getName(),
                    CustomExtValCrossValidationModuleConfiguration.class.getName());

        }
    }

    @Override
    protected ExtValCrossValidationModuleConfiguration getCustomCrossValidationModuleConfiguration()
    {
        // Don't specify the custom config here. We explicitly want to test the
        // web.xml parameter.
        return null;
    }

    @Test
    public void testExtValCrossValidationModuleConfigurationCustomDefault()
    {
        Assert.assertFalse(ExtValCrossValidationModuleConfiguration.get().deactivateCrossvalidation());
    }

    @Test
    public void testExtValCrossValidationModuleConfigurationCustomCustomConfig()
    {
        Assert.assertTrue(ExtValCrossValidationModuleConfiguration.get().deactivateCrossvalidation());
    }

}
