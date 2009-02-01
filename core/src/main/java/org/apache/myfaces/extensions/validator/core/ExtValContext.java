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

import org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticConfiguration;
import org.apache.myfaces.extensions.validator.core.interceptor.RendererInterceptor;
import org.apache.myfaces.extensions.validator.core.interceptor.ValidationExceptionInterceptor;
import org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder;
import org.apache.myfaces.extensions.validator.core.factory.FactoryFinder;
import org.apache.myfaces.extensions.validator.core.factory.DefaultFactoryFinder;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticConfigurationNames;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.API)
public class ExtValContext
{
    private final Log logger = LogFactory.getLog(getClass());

    private static ExtValContext extValContext = new ExtValContext();

    private FactoryFinder factoryFinder = DefaultFactoryFinder.getInstance();
    private Map<String, RendererInterceptor> rendererInterceptors = new HashMap<String, RendererInterceptor>();
    private List<String> deniedInterceptors = new ArrayList<String>();
    private List<ProcessedInformationRecorder> processedInformationRecorders =
        new ArrayList<ProcessedInformationRecorder>();

    private List<ComponentInitializer> componentInitializers;
    private List<ValidationExceptionInterceptor> validationExceptionInterceptors;

    private Map<String, Object> globalProperties = new HashMap<String, Object>();

    private Map<StaticConfigurationNames, List<StaticConfiguration<String, String>>> staticConfigMap
        = new HashMap<StaticConfigurationNames, List<StaticConfiguration<String, String>>>();


    private void lazyInitComponentInitializers()
    {
        if(this.componentInitializers != null)
        {
            return;
        }

        this.componentInitializers = new ArrayList<ComponentInitializer>();
        List<String> componentInitializerClassNames = new ArrayList<String>();
        componentInitializerClassNames
            .add(WebXmlParameter.CUSTOM_COMPONENT_INITIALIZER);
        componentInitializerClassNames
            .add(ExtValContext.getContext().getInformationProviderBean().get(CustomInformation.COMPONENT_INITIALIZER));

        ComponentInitializer componentInitializer;
        for (String componentInitializerName : componentInitializerClassNames)
        {
            componentInitializer =
                (ComponentInitializer) ClassUtils.tryToInstantiateClassForName(componentInitializerName);

            if (componentInitializer != null)
            {
                componentInitializers.add(componentInitializer);

                if(logger.isTraceEnabled())
                {
                    logger.trace(componentInitializer.getClass().getName() + " added");
                }
            }
        }
    }

    private void lazyInitValidationExceptionInterceptors()
    {
        if(this.validationExceptionInterceptors != null)
        {
            return;
        }

        this.validationExceptionInterceptors = new ArrayList<ValidationExceptionInterceptor>();
        List<String> validationExceptionInterceptorClassNames = new ArrayList<String>();

        validationExceptionInterceptorClassNames
            .add(WebXmlParameter.CUSTOM_VALIDATION_EXCEPTION_INTERCEPTOR);
        validationExceptionInterceptorClassNames
            .add(ExtValContext.getContext().getInformationProviderBean().get(
                    CustomInformation.VALIDATION_EXCEPTION_INTERCEPTOR));

        ValidationExceptionInterceptor validationExceptionInterceptor;
        for (String validationExceptionInterceptorName : validationExceptionInterceptorClassNames)
        {
            validationExceptionInterceptor =
                (ValidationExceptionInterceptor)
                        ClassUtils.tryToInstantiateClassForName(validationExceptionInterceptorName);

            if (validationExceptionInterceptor != null)
            {
                validationExceptionInterceptors.add(validationExceptionInterceptor);

                if(logger.isTraceEnabled())
                {
                    logger.trace(validationExceptionInterceptor.getClass().getName() + " added");
                }
            }
        }
    }

    public static ExtValContext getContext()
    {
        return extValContext;
    }

    public FactoryFinder getFactoryFinder()
    {
        return factoryFinder;
    }

    public void setFactoryFinder(FactoryFinder factoryFinder)
    {
        if(factoryFinder != null)
        {
            this.factoryFinder = factoryFinder;
        }
    }

    public List<RendererInterceptor> getRendererInterceptors()
    {
        return new ArrayList<RendererInterceptor>(rendererInterceptors.values());
    }

    public boolean registerRendererInterceptor(RendererInterceptor rendererInterceptor)
    {
        synchronized (ExtValContext.class)
        {
            if (deniedInterceptors.contains(rendererInterceptor.getInterceptorId()))
            {
                return false;
            }

            rendererInterceptors.put(rendererInterceptor.getInterceptorId(), rendererInterceptor);
        }
        return true;
    }

    public void deregisterRendererInterceptor(Class rendererInterceptorClass)
    {
        RendererInterceptor rendererInterceptor =
            (RendererInterceptor) ClassUtils.tryToInstantiateClass(rendererInterceptorClass);

        synchronized (ExtValContext.class)
        {
            rendererInterceptors.remove(rendererInterceptor.getInterceptorId());
        }
    }

    //if an interceptor hasn't been registered so far, it should be denied at future registrations
    public void denyRendererInterceptor(Class rendererInterceptorClass)
    {
        RendererInterceptor rendererInterceptor =
            (RendererInterceptor) ClassUtils.tryToInstantiateClass(rendererInterceptorClass);

        synchronized (ExtValContext.class)
        {
            deniedInterceptors.add(rendererInterceptor.getInterceptorId());
        }
        deregisterRendererInterceptor(rendererInterceptorClass);
    }

    public void addComponentInitializer(ComponentInitializer componentInitializer)
    {
        lazyInitComponentInitializers();
        this.componentInitializers.add(componentInitializer);
    }

    public List<ComponentInitializer> getComponentInitializers()
    {
        lazyInitComponentInitializers();
        return componentInitializers;
    }

    public void addValidationExceptionInterceptor(ValidationExceptionInterceptor validationExceptionInterceptor)
    {
        lazyInitValidationExceptionInterceptors();
        this.validationExceptionInterceptors.add(validationExceptionInterceptor);
    }

    public List<ValidationExceptionInterceptor> getValidationExceptionInterceptors()
    {
        lazyInitValidationExceptionInterceptors();
        return validationExceptionInterceptors;
    }

    public List<ProcessedInformationRecorder> getProcessedInformationRecorders()
    {
        return processedInformationRecorders;
    }

    public void addProcessedInformationRecorder(ProcessedInformationRecorder processedInformationRecorder)
    {
        this.processedInformationRecorders.add(processedInformationRecorder);
    }

    public InformationProviderBean getInformationProviderBean()
    {
        Map applicationMap = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        InformationProviderBean bean = (InformationProviderBean) applicationMap.get(InformationProviderBean.BEAN_NAME);

        if (bean == null)
        {
            return initInformationProviderBean(applicationMap);
        }
        return bean;
    }

    private InformationProviderBean initInformationProviderBean(Map applicationMap)
    {
        List<String> informationProviderBeanClassNames = new ArrayList<String>();

        informationProviderBeanClassNames.add(WebXmlParameter.CUSTOM_INFORMATION_PROVIDER_BEAN);
        informationProviderBeanClassNames.add(InformationProviderBean.CUSTOM_BEAN);

        InformationProviderBean informationProviderBean;
        for (String className : informationProviderBeanClassNames)
        {
            informationProviderBean = (InformationProviderBean) ClassUtils.tryToInstantiateClassForName(className);

            if (informationProviderBean != null)
            {
                applicationMap.put(InformationProviderBean.BEAN_NAME, informationProviderBean);
                return informationProviderBean;
            }
        }

        tryToInitCustomConfiguredInformationProviderBeanClassName(applicationMap);

        if(applicationMap.containsKey(InformationProviderBean.BEAN_NAME))
        {
            return (InformationProviderBean)applicationMap.get(InformationProviderBean.BEAN_NAME);
        }
        return new InformationProviderBean();
    }

    private void tryToInitCustomConfiguredInformationProviderBeanClassName(Map applicationMap)
    {
        InformationProviderBean bean = (InformationProviderBean) ExtValUtils.getELHelper()
            .getBean(InformationProviderBean.CUSTOM_BEAN.replace(".", "_"));

        if(bean != null)
        {
            applicationMap.put(InformationProviderBean.BEAN_NAME, bean);
        }
    }

    public List<StaticConfiguration<String, String>> getStaticConfiguration(StaticConfigurationNames name)
    {
        if(!this.staticConfigMap.containsKey(name))
        {
            List<StaticConfiguration<String, String>> staticConfigList =
                    new ArrayList<StaticConfiguration<String, String>>();
            this.staticConfigMap.put(name, staticConfigList);
        }
        return this.staticConfigMap.get(name);
    }

    public void addStaticConfiguration(StaticConfigurationNames name, StaticConfiguration<String, String> staticConfig)
    {
        synchronized (this)
        {
            List<StaticConfiguration<String, String>> staticConfigList;
            if(!this.staticConfigMap.containsKey(name))
            {
                staticConfigList = new ArrayList<StaticConfiguration<String, String>>();
                this.staticConfigMap.put(name, staticConfigList);
            }
            this.staticConfigMap.get(name).add(staticConfig);
        }
    }

    public boolean addGlobalProperty(String name, Object value)
    {
        return addGlobalProperty(name , value, true);
    }

    public boolean addGlobalProperty(String name, Object value, boolean forceOverride)
    {
        if(this.globalProperties.containsKey(name))
        {
            if(!forceOverride)
            {
                return false;
            }
            else
            {
                if(this.logger.isInfoEnabled())
                {
                    logger.info("override global property '" + name + "'");
                }
            }
        }

        this.globalProperties.put(name, value);
        return true;
    }

    public Object getGlobalProperty(String name)
    {
        return this.globalProperties.get(name);
    }
}
