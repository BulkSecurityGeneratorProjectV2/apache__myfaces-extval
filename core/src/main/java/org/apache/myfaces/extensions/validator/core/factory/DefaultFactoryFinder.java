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
package org.apache.myfaces.extensions.validator.core.factory;

import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.core.metadata.extractor.DefaultComponentMetaDataExtractorFactory;
import org.apache.myfaces.extensions.validator.core.WebXmlParameter;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.CustomInfo;
import org.apache.myfaces.extensions.validator.core.interceptor.DefaultValidationExceptionInterceptorFactory;
import org.apache.myfaces.extensions.validator.core.el.DefaultELHelperFactory;
import org.apache.myfaces.extensions.validator.core.renderkit.DefaultRenderKitWrapperFactory;
import org.apache.myfaces.extensions.validator.core.initializer.component.DefaultComponentInitializerFactory;
import org.apache.myfaces.extensions.validator.core.metadata.transformer.DefaultMetaDataTransformerFactory;
import org.apache.myfaces.extensions.validator.core.validation.strategy.DefaultValidationStrategyFactory;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.DefaultMessageResolverFactory;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
//dynamic approach to create the factories during the first request, when a faces-context is available
@UsageInformation(UsageCategory.INTERNAL)
public class DefaultFactoryFinder implements FactoryFinder
{
    protected final Log logger = LogFactory.getLog(getClass());
    protected Map<FactoryNames, Object> factoryMap = new HashMap<FactoryNames, Object>();

    public DefaultFactoryFinder()
    {
        if(logger.isDebugEnabled())
        {
            logger.debug(getClass().getName() + " instantiated");
        }
    }

    public final <T> T getFactory(FactoryNames factoryName, Class<T> targetClass)
    {
        if(!(factoryMap.containsKey(factoryName)))
        {
            initFactory(factoryName);
        }

        return (T)factoryMap.get(factoryName);
    }

    private void initFactory(FactoryNames factoryName)
    {
        Object factory = null;
        switch (factoryName)
        {
            case COMPONENT_META_DATA_EXTRACTOR_FACTORY:
                factory = createComponentMetaDataExtractorFactory();
                break;

            case VALIDATION_STRATEGY_FACTORY:
                factory = createValidationStrategyFactory();
                break;

            case MESSAGE_RESOLVER_FACTORY:
                factory = createMessageResolverFactory();
                break;

            case META_DATA_TRANSFORMER_FACTORY:
                factory = createMetaDataTransformerFactory();
                break;

            case COMPONENT_INITIALIZER_FACTORY:
                factory = createComponentInitializerFactory();
                break;

            case VALIDATION_EXCEPTION_INTERCEPTOR_FACTORY:
                factory = createValidationExceptionInterceptorFactory();
                break;

            case RENDERKIT_WRAPPER_FACTORY:
                factory = createRenderKitWrapperFactory();
                break;

            case EL_HELPER_FACTORY:
                factory = createELHelperFactory();
                break;
            default: //required by checkstyle
        }

        if(factory == null)
        {
            throw new IllegalStateException("not possible to create factory " + factoryName);
        }

        factoryMap.put(factoryName, factory);
    }

    protected Object createComponentMetaDataExtractorFactory()
    {
        Object factory = null;

        List<String> metaDataExtractorFactoryClassNames = new ArrayList<String>();

        metaDataExtractorFactoryClassNames.add(WebXmlParameter.CUSTOM_COMPONENT_META_DATA_EXTRACTOR_FACTORY);
        metaDataExtractorFactoryClassNames
            .add(ExtValContext.getContext().getInformationProviderBean()
                .get(CustomInfo.COMPONENT_META_DATA_EXTRACTOR_FACTORY));
        metaDataExtractorFactoryClassNames.add(DefaultComponentMetaDataExtractorFactory.class.getName());

        for (String className : metaDataExtractorFactoryClassNames)
        {
            factory = ClassUtils.tryToInstantiateClassForName(className);

            if (factory != null)
            {
                break;
            }
        }
        return factory;
    }

    protected Object createValidationStrategyFactory()
    {
        Object factory = null;

        List<String> validationStrategyFactoryClassNames = new ArrayList<String>();

        validationStrategyFactoryClassNames.add(WebXmlParameter.CUSTOM_VALIDATION_STRATEGY_FACTORY);
        validationStrategyFactoryClassNames
            .add(ExtValContext.getContext().getInformationProviderBean().get(CustomInfo.VALIDATION_STRATEGY_FACTORY));
        validationStrategyFactoryClassNames.add(DefaultValidationStrategyFactory.class.getName());

        for (String className : validationStrategyFactoryClassNames)
        {
            factory = ClassUtils.tryToInstantiateClassForName(className);

            if (factory != null)
            {
                break;
            }
        }

        return factory;
    }

    protected Object createMessageResolverFactory()
    {
        Object factory = null;
        List<String> messageResolverFactoryClassNames = new ArrayList<String>();

        messageResolverFactoryClassNames.add(WebXmlParameter.CUSTOM_MESSAGE_RESOLVER_FACTORY);
        messageResolverFactoryClassNames
            .add(ExtValContext.getContext().getInformationProviderBean().get(CustomInfo.MESSAGE_RESOLVER_FACTORY));
        messageResolverFactoryClassNames
            .add(DefaultMessageResolverFactory.class.getName());

        for (String className : messageResolverFactoryClassNames)
        {
            factory = ClassUtils.tryToInstantiateClassForName(className);

            if (factory != null)
            {
                break;
            }
        }

        return factory;
    }

    protected Object createMetaDataTransformerFactory()
    {
        Object factory = null;
        List<String> metaDataTransformerFactoryClassNames = new ArrayList<String>();

        metaDataTransformerFactoryClassNames.add(WebXmlParameter.CUSTOM_META_DATA_TRANSFORMER_FACTORY );
        metaDataTransformerFactoryClassNames
            .add(ExtValContext.getContext().getInformationProviderBean().get(CustomInfo.META_DATA_TRANSFORMER_FACTORY));
        metaDataTransformerFactoryClassNames.add(DefaultMetaDataTransformerFactory.class.getName());

        for (String className : metaDataTransformerFactoryClassNames)
        {
            factory = ClassUtils.tryToInstantiateClassForName(className);

            if (factory != null)
            {
                break;
            }
        }

        return factory;
    }

    protected Object createComponentInitializerFactory()
    {
        Object factory = null;
        List<String> componentInitializerFactoryClassNames = new ArrayList<String>();

        componentInitializerFactoryClassNames.add(WebXmlParameter.CUSTOM_COMPONENT_INITIALIZER_FACTORY);
        componentInitializerFactoryClassNames
            .add(ExtValContext.getContext().getInformationProviderBean().get(CustomInfo.COMPONENT_INITIALIZER_FACTORY));
        componentInitializerFactoryClassNames.add(DefaultComponentInitializerFactory.class.getName());

        for (String className : componentInitializerFactoryClassNames)
        {
            factory = ClassUtils.tryToInstantiateClassForName(className);

            if (factory != null)
            {
                break;
            }
        }

        return factory;
    }

    private Object createValidationExceptionInterceptorFactory()
    {
        Object factory = null;
        List<String> validationExceptionInterceptorFactoryClassNames = new ArrayList<String>();

        validationExceptionInterceptorFactoryClassNames
                .add(WebXmlParameter.CUSTOM_VALIDATION_EXCEPTION_INTERCEPTOR_FACTORY);
        validationExceptionInterceptorFactoryClassNames
            .add(ExtValContext.getContext().getInformationProviderBean().get(
                    CustomInfo.VALIDATION_EXCEPTION_INTERCEPTOR_FACTORY));
        validationExceptionInterceptorFactoryClassNames
                .add(DefaultValidationExceptionInterceptorFactory.class.getName());

        for (String className : validationExceptionInterceptorFactoryClassNames)
        {
            factory = ClassUtils.tryToInstantiateClassForName(className);

            if (factory != null)
            {
                break;
            }
        }

        return factory;
    }

    protected Object createRenderKitWrapperFactory()
    {
        return new DefaultRenderKitWrapperFactory();
    }

    private Object createELHelperFactory()
    {
        return new DefaultELHelperFactory();
    }
}
