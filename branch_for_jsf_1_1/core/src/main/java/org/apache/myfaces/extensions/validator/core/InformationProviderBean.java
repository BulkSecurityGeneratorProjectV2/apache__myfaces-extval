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
package org.apache.myfaces.extensions.validator.core;

import org.apache.myfaces.extensions.validator.ExtValInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.HashMap;

/**
 * centralized in order that these information aren't spread over the complete code base
 * + some of them can be customized within a custom impl. of the bean
 * (extend this class and provide it via convention or web.xml)
 * <p/>
 * the static api should only be used
 *
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation({UsageCategory.API, UsageCategory.CUSTOMIZABLE})
public class InformationProviderBean
{
    protected final Log logger = LogFactory.getLog(getClass());

    public static final String BEAN_NAME = ExtValInformation.EXTENSIONS_VALIDATOR_BASE_PACKAGE_NAME
        + "." + InformationProviderBean.class.getSimpleName();
    //custom class which is an optional replacement for this class (has to extend this class)
    public static final String CUSTOM_BEAN = (ExtValInformation.EXTENSIONS_VALIDATOR_BASE_PACKAGE_NAME
        + ".custom." + InformationProviderBean.class.getSimpleName());

    public InformationProviderBean()
    {
        if(logger.isDebugEnabled())
        {
            logger.debug(getClass().getName() + " instantiated");
        }

        setupCustomizableInformation();
        applyCustomValues(this.customizableInfos);
    }

    private Map<CustomInformation, String> customizableInfos = new HashMap<CustomInformation, String>();

    private void setupCustomizableInformation()
    {
        String basePackage = WebXmlParameter.CUSTOM_BASE_PACKAGE;

        if (basePackage == null)
        {
            basePackage = ExtValInformation.EXTENSIONS_VALIDATOR_BASE_PACKAGE_NAME + ".custom.";
        }
        if (!basePackage.endsWith("."))
        {
            basePackage = basePackage + ".";
        }

        customizableInfos.put(CustomInformation.BASE_PACKAGE, basePackage);
        customizableInfos.put(CustomInformation.EXTVAL_CONTEXT, "customExtValContext");
        
        customizableInfos.put(CustomInformation.COMPONENT_META_DATA_EXTRACTOR,
                "ComponentMetaDataExtractor");
        customizableInfos.put(CustomInformation.VALIDATION_PARAMETER_EXTRACTOR,
                "VALIDATION_PARAMETER_EXTRACTOR");

        customizableInfos.put(CustomInformation.VALIDATION_STRATEGY_POSTFIX,
                "ValidationStrategy");
        customizableInfos.put(CustomInformation.META_DATA_TRANSFORMER_POSTFIX,
                "MetaDataTransformer");
        customizableInfos.put(CustomInformation.VALIDATION_ERROR_MESSAGE_RESOLVER_POSTFIX,
                "ValidationErrorMessageResolver");

        customizableInfos.put(CustomInformation.COMPONENT_INITIALIZER,
                "ComponentInitializer");
        customizableInfos.put(CustomInformation.VALIDATION_EXCEPTION_INTERCEPTOR,
                "ValidationExceptionInterceptor");
        customizableInfos.put(CustomInformation.PROPERTY_VALIDATION_INTERCEPTOR,
                "PropertyValidationInterceptor");
        customizableInfos.put(CustomInformation.META_DATA_EXTRACTION_INTERCEPTOR,
                "MetaDataExtractionInterceptor");

        customizableInfos.put(CustomInformation.VALIDATION_STRATEGY_TO_MSG_RESOLVER_NAME_MAPPER,
            "ValidationStrategyToMsgResolverNameMapper");
        customizableInfos.put(CustomInformation.META_DATA_TO_VALIDATION_STRATEGY_NAME_MAPPER,
            "MetaDataToValidationStrategyNameMapper");
        customizableInfos.put(CustomInformation.VALIDATION_STRATEGY_TO_META_DATA_TRANSFORMER_NAME_MAPPER,
            "ValidationStrategyToMetaDataTransformerNameMapper");

        customizableInfos.put(CustomInformation.STARTUP_LISTENER,
                "StartupListener");

        customizableInfos.put(CustomInformation.MESSAGE_RESOLVER_FACTORY,
                "MessageResolverFactory");
        customizableInfos.put(CustomInformation.VALIDATION_STRATEGY_FACTORY,
                "ValidationStrategyFactory");
        customizableInfos.put(CustomInformation.COMPONENT_META_DATA_EXTRACTOR_FACTORY,
                "ComponentMetaDataExtractorFactory");
        customizableInfos.put(CustomInformation.VALIDATION_PARAMETER_EXTRACTOR_FACTORY,
                "VALIDATION_PARAMETER_EXTRACTOR_FACTORY");
        customizableInfos.put(CustomInformation.META_DATA_TRANSFORMER_FACTORY,
                "MetaDataTransformerFactory");
        customizableInfos.put(CustomInformation.FACES_MESSAGE_FACTORY,
                "FacesMessageFactory");

        //conventions (the rest of the conventions are built with the help of name mappers,...
        customizableInfos.put(CustomInformation.MESSAGE_BUNDLE_NAME,
                "validation_messages");
        //static strategy mappings (name of property files)
        customizableInfos.put(CustomInformation.STATIC_STRATEGY_MAPPING_SOURCE,
                "strategy_mappings");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void applyCustomValues(Map<CustomInformation, String> map)
    {
        //override to customize information
    }

    public final String get(CustomInformation customInformation)
    {
        String value = customizableInfos.get(customInformation);

        switch (customInformation)
        {
            case BASE_PACKAGE:
                return value;

            case EXTVAL_CONTEXT:
                return value;

            /*
             * postfix used by the SimpleAnnotationToValidationStrategyNameMapper
             * the SimpleAnnotationToValidationStrategyNameMapper is for custom strategies only
             * (not for public validation modules)
             * so it's fine to customize it
             */
            case VALIDATION_STRATEGY_POSTFIX:
                return value;

            case VALIDATION_ERROR_MESSAGE_RESOLVER_POSTFIX:
                return value;

            case META_DATA_TRANSFORMER_POSTFIX:
                return value;

            default:
                return customizableInfos.get(CustomInformation.BASE_PACKAGE) + value;
        }
    }
}
