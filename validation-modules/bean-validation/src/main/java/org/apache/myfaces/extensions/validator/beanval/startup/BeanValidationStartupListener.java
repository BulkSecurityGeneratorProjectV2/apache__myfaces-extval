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
package org.apache.myfaces.extensions.validator.beanval.startup;

import org.apache.myfaces.extensions.validator.beanval.BeanValidationInterceptor;
import org.apache.myfaces.extensions.validator.beanval.validation.ModelValidationPhaseListener;
import org.apache.myfaces.extensions.validator.beanval.interceptor.PropertyValidationGroupProvider;
import org.apache.myfaces.extensions.validator.beanval.metadata.transformer.mapper
        .DefaultBeanValidationStrategyToMetaDataTransformerNameMapper;
import org.apache.myfaces.extensions.validator.beanval.storage.DefaultModelValidationStorageManager;
import org.apache.myfaces.extensions.validator.beanval.storage.ModelValidationStorage;
import org.apache.myfaces.extensions.validator.beanval.storage.mapper.BeanValidationGroupStorageNameMapper;
import org.apache.myfaces.extensions.validator.beanval.storage.mapper.ModelValidationStorageNameMapper;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.factory.AbstractNameMapperAwareFactory;
import org.apache.myfaces.extensions.validator.core.factory.FactoryNames;
import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.storage.GroupStorage;
import org.apache.myfaces.extensions.validator.core.storage.StorageManager;
import org.apache.myfaces.extensions.validator.core.storage.StorageManagerHolder;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.JsfUtils;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@ToDo(value = Priority.HIGH, description = "add optional web.xml param to deactivate" +
        "DefaultBeanValidationStrategyToMetaDataTransformerNameMapper")
@UsageInformation(UsageCategory.INTERNAL)
public class BeanValidationStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = -5025748399876833394L;

    protected void init()
    {
        if(!"true".equalsIgnoreCase(org.apache.myfaces.extensions.validator.beanval.WebXmlParameter
                .DEACTIVATE_DEFAULT_BEAN_VALIDATION_INTEGRATION))
        {
            registerBeanValidationInterceptor();
            registerValidationGroupProvider();
            registerMetaDataTransformerNameMapper();
            registerGroupStorageNameMapper();
            registerModelValidationStorageNameMapper();
            registerModelValidationPhaseListener();
        }
    }

    private void registerBeanValidationInterceptor()
    {
        ExtValContext.getContext().registerRendererInterceptor(new BeanValidationInterceptor());
    }

    private void registerValidationGroupProvider()
    {
        ExtValContext.getContext().addPropertyValidationInterceptor(new PropertyValidationGroupProvider());
    }

    private void registerMetaDataTransformerNameMapper()
    {
        ExtValUtils.registerValidationStrategyToMetaDataTransformerNameMapper(
                new DefaultBeanValidationStrategyToMetaDataTransformerNameMapper());
    }

    @SuppressWarnings({"unchecked"})
    private void registerGroupStorageNameMapper()
    {
        StorageManager storageManager = getStorageManagerHolder().getStorageManager(GroupStorage.class);

        if (storageManager instanceof AbstractNameMapperAwareFactory)
        {
            ((AbstractNameMapperAwareFactory<String>) storageManager)
                    .register(new BeanValidationGroupStorageNameMapper());
        }
        else
        {
            if (this.logger.isWarnEnabled())
            {
                this.logger.warn(storageManager.getClass().getName() +
                        " has to implement AbstractNameMapperAwareFactory " + getClass().getName() +
                        " couldn't register " + BeanValidationGroupStorageNameMapper.class.getName());
            }
        }
    }

    private void registerModelValidationStorageNameMapper()
    {
        DefaultModelValidationStorageManager modelValidationStorageManager = new DefaultModelValidationStorageManager();
        modelValidationStorageManager.register(new ModelValidationStorageNameMapper());
        getStorageManagerHolder().setStorageManager(ModelValidationStorage.class, modelValidationStorageManager, false);
    }

    private StorageManagerHolder getStorageManagerHolder()
    {
        return (ExtValContext.getContext()
                .getFactoryFinder()
                .getFactory(FactoryNames.STORAGE_MANAGER_FACTORY, StorageManagerHolder.class));
    }

    private void registerModelValidationPhaseListener()
    {
        JsfUtils.registerPhaseListener(new ModelValidationPhaseListener());
    }
}
