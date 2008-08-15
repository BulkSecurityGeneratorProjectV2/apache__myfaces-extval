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
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * centralized in order that these information arn't spread over the complete code base
 * + some of them can be customized within a custom impl. of the bean 
 * (extend this class and provide it via convention or web.xml)
 * <p/>
 * the static api should only be used
 *
 * @author Gerhard Petracek
 */
public class InformationProviderBean
{
    public static final String BEAN_NAME = ExtValInformation.EXTENSIONS_VALIDATOR_BASE_PACKAGE_NAME
            + "." + InformationProviderBean.class.getSimpleName();
    //custom class which is an optional replacement for this class (has to extend this class)
    public static final String CUSTOM_BEAN = (ExtValInformation.EXTENSIONS_VALIDATOR_BASE_PACKAGE_NAME
            + ".custom." + InformationProviderBean.class.getSimpleName())
            .replace(".", "_");
    private String basePackage = WebXmlParameter.CUSTOM_EXTENSION_BASE_PACKAGE;

    public InformationProviderBean()
    {
        if (this.basePackage == null)
        {
            this.basePackage = ExtValInformation.EXTENSIONS_VALIDATOR_BASE_PACKAGE_NAME
                    + ".custom.";
        }
        if (!this.basePackage.endsWith("."))
        {
            this.basePackage = this.basePackage + ".";
        }
    }

    public String getBasePackage()
    {
        return basePackage;
    }

    public String getCustomAnnotationExtractorFactory()
    {
        return this.basePackage + "AnnotationExtractorFactory";
    }

    public String getCustomAnnotationExtractor()
    {
        return this.basePackage + "AnnotationExtractor";
    }

    /*
     * postfix used by the SimpleAnnotationToValidationStrategyNameMapper
     * the SimpleAnnotationToValidationStrategyNameMapper is for custom strategies 
     * only (not for public validation modules)
     * so it's fine to customize it
     */
    public String getValidationStrategyPostfix()
    {
        return "ValidationStrategy";
    }

    /*
     * name mapper
     */
    public String getCustomValidationStrategyToMsgResolverNameMapper()
    {
        return this.basePackage + "ValidationStrategyToMsgResolverNameMapper";
    }

    public String getCustomAnnotationToValidationStrategyNameMapper()
    {
        return this.basePackage + "AnnotationToValidationStrategyNameMapper";
    }

    @Deprecated
    public String getCustomAdapterNameMapper()
    {
        return this.basePackage + "AdapterNameMapper";
    }

    /*
     * factories
     */
    public String getCustomMessageResolverFactory()
    {
        return this.basePackage + "MessageResolverFactory";
    }

    public String getCustomValidationStrategyFactory()
    {
        return this.basePackage + "ValidationStrategyFactory";
    }

    @Deprecated
    public String getCustomConverterAdapterFactory()
    {
        return this.basePackage + "ConverterAdapterFactory";
    }

    //TODO
    /*
     * conventions (the rest of the conventions are built with the help of name mappers,...
     */
    public String getConventionForMessageBundle()
    {
        return this.basePackage + "validation_messages";
    }

    /*
     * static strategy mappings (name of property files)
     */
    public String getCustomStaticStrategyMappingSource()
    {
        return this.basePackage + "strategy_mappings";
    }

    private List<String> staticStrategyMappings = new ArrayList<String>();

    /*
     * final methods
     */
    //TODO
    public final String getConventionForModuleMessageBundle(String packageName)
    {
        String newPackageName;
        if (packageName.endsWith(".resolver"))
        {
            newPackageName = packageName.replace(".resolver", ".bundle");
        }
        else
        {
            newPackageName = packageName.replace(".resolver.", ".bundle.");
        }

        return newPackageName + ".validation_messages";
    }

    public final List<String> getStaticStrategyMappingSources()
    {
        return this.staticStrategyMappings;
    }

    public final void addStaticStrategyMappingSource(String resourceBundleName)
    {
        synchronized (this)
        {
            this.staticStrategyMappings.add(resourceBundleName);
        }
    }

    public final boolean containsStaticStrategyMappingSource(
            String resourceBundleName)
    {
        return this.staticStrategyMappings.contains(resourceBundleName);
    }

    /**
     * use a custom name mapper to implement custom conventions
     */
    public final String getConventionNameForMessageResolverPackage(
            Class<? extends ValidationStrategy> validationStrategyClass,
            String targetClassName)
    {
        String resolverName = validationStrategyClass.getName();

        resolverName = resolverName.replace(".strategy.", ".message.resolver.");

        if (targetClassName == null)
        {
            //TODO
            return null;
        }
        return resolverName.substring(0, resolverName.lastIndexOf(".")) + "."
                + targetClassName;
    }

    /**
     * use a custom name mapper to implement custom conventions
     */
    public final String getConventionNameForMessageResolverClass(
            String strategyClassName)
    {
        if (strategyClassName.endsWith("ValidationStrategy"))
        {
            return strategyClassName.substring(0,
                    strategyClassName.length() - 18)
                    + "ValidationErrorMessageResolver";
        }
        else if (strategyClassName.endsWith("Strategy"))
        {
            return strategyClassName.substring(0,
                    strategyClassName.length() - 8)
                    + "ValidationErrorMessageResolver";
        }
        return strategyClassName;
    }

    /**
     * use a custom name mapper to implement custom conventions
     */
    public final String getConventionNameForValidationStrategy(
            Annotation annotation)
    {
        return annotation.annotationType().getName().replace(".annotation.",
                ".strategy.")
                + "Strategy";
    }
}
