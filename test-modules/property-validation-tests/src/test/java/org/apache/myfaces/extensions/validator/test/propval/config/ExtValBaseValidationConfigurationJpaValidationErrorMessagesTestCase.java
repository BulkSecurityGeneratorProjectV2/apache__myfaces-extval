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

import junit.framework.Test;

import org.apache.myfaces.extensions.validator.ExtValInformation;
import org.apache.myfaces.extensions.validator.baseval.DefaultExtValBaseValidationModuleConfiguration;
import org.apache.myfaces.extensions.validator.baseval.ExtValBaseValidationModuleConfiguration;
import org.apache.myfaces.extensions.validator.baseval.message.resolver.JpaValidationErrorMessageResolver;
import org.apache.myfaces.extensions.validator.test.base.util.ClassLoaderTestSuite;

/**
 * 
 * since v4
 *
 */
public class ExtValBaseValidationConfigurationJpaValidationErrorMessagesTestCase extends
        ExtValBaseValidationConfigurationTestCase
{

    private static final String WEB_XML = "Web.XML";
    private static final String CUSTOM_CONFIG = "Custom config";

    public ExtValBaseValidationConfigurationJpaValidationErrorMessagesTestCase(String name)
    {
        super(name);
    }

    public static class VisibleJpaValidationErrorMessageResolver extends JpaValidationErrorMessageResolver
    {

        @Override
        public String getCustomBaseName()
        {
            return super.getCustomBaseName();
        }

    }

    @Override
    protected void addInitializationParameters()
    {
        super.addInitializationParameters();
        if (needXmlParameters())
        {
            addInitParameter(ExtValInformation.WEBXML_PARAM_PREFIX + ".JPA_VALIDATION_ERROR_MESSAGES", WEB_XML);

        }
    }

    @Override
    protected ExtValBaseValidationModuleConfiguration getCustomBaseValidationModuleConfiguration()
    {
        return new DefaultExtValBaseValidationModuleConfiguration()
        {

            @Override
            public String jpaValidationErrorMessages()
            {
                return CUSTOM_CONFIG;
            }

        };
    }

    public void testExtValBaseValidationConfigurationJpaValidationErrorMessagesDefault()
    {
        VisibleJpaValidationErrorMessageResolver messageResolver = new VisibleJpaValidationErrorMessageResolver();
        assertNull(messageResolver.getCustomBaseName());
    }

    public void testExtValBaseValidationConfigurationJpaValidationErrorMessagesWebXml()
    {
        VisibleJpaValidationErrorMessageResolver messageResolver = new VisibleJpaValidationErrorMessageResolver();
        assertEquals(WEB_XML, messageResolver.getCustomBaseName());
    }

    public void testExtValBaseValidationConfigurationJpaValidationErrorMessagesCustomConfig()
    {
        VisibleJpaValidationErrorMessageResolver messageResolver = new VisibleJpaValidationErrorMessageResolver();
        assertEquals(CUSTOM_CONFIG, messageResolver.getCustomBaseName());
    }

    public static Test suite()
    {

        return new ClassLoaderTestSuite(ExtValBaseValidationConfigurationJpaValidationErrorMessagesTestCase.class);
    }

}
