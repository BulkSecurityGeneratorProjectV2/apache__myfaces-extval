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
package org.apache.myfaces.extensions.validator;

import org.apache.myfaces.extensions.validator.baseval.WebXmlParameter;
import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.interceptor.ValidationInterceptor;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticResourceBundleConfiguration;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticConfiguration;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticConfigurationNames;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.crossval.recorder.CrossValidationUserInputRecorder;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.INTERNAL)
public class PropertyValidationModuleStartupListener extends AbstractStartupListener
{
    protected void init()
    {
        ExtValContext.getContext().addProcessedInformationRecorder(new CrossValidationUserInputRecorder());

        initStaticStrategyMappings();
        initDefaultComponentInitializer();
        initDefaultValidationExceptionInterceptor();
        addSkipValidationSupport();
    }

    private void initStaticStrategyMappings()
    {
        String jpaBasedValidation = WebXmlParameter.DEACTIVATE_JPA_BASED_VALIDATION;
        if (jpaBasedValidation == null
                || !jpaBasedValidation.equalsIgnoreCase("true"))
        {
            StaticConfiguration<String, String> staticConfig = new StaticResourceBundleConfiguration();
            staticConfig.setSourceOfMapping(
                ExtValInformation.EXTENSIONS_VALIDATOR_BASE_PACKAGE_NAME +".jpa_strategy_mappings");

            ExtValContext.getContext().addStaticConfiguration(
             StaticConfigurationNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG, staticConfig);
        }
    }

    @ToDo(value = Priority.MEDIUM, description = "web.xml parameter to deactivate it")
    private void initDefaultComponentInitializer()
    {
        ExtValContext.getContext().addComponentInitializer(new HtmlCoreComponentsComponentInitializer());
    }

    @ToDo(value = Priority.MEDIUM, description = "web.xml parameter to deactivate it")
    private void initDefaultValidationExceptionInterceptor()
    {
        ExtValContext.getContext().addValidationExceptionInterceptor(
                new HtmlCoreComponentsValidationExceptionInterceptor());
    }

    private void addSkipValidationSupport()
    {
        if(logger.isInfoEnabled())
        {
            logger.info("adding support for @SkipValidation");
        }

        ExtValContext.getContext().denyRendererInterceptor(ValidationInterceptor.class);
        ExtValContext.getContext().registerRendererInterceptor(new ValidationInterceptorWithSkipValidationSupport());
    }
}
