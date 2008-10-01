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
package org.apache.myfaces.extensions.validator.core.initializer.component;

import org.apache.myfaces.extensions.validator.core.mapper.ClassMappingFactory;
import org.apache.myfaces.extensions.validator.core.WebXmlParameter;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.LogUtils;

import javax.faces.component.UIComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation({UsageCategory.INTERNAL, UsageCategory.CUSTOMIZABLE})
public class DefaultComponentInitializerFactory implements
    ClassMappingFactory<UIComponent, ComponentInitializer>
{
    private static Map<String, ComponentInitializer> componentToComponentInitializerMapping
        = new HashMap<String, ComponentInitializer>();
    private static List<String> componentInitializerClassNames = new ArrayList<String>();

    static
    {
        componentInitializerClassNames
            .add(WebXmlParameter.CUSTOM_COMPONENT_INITIALIZER);
        componentInitializerClassNames
            .add(ExtValContext.getContext().getInformationProviderBean().getCustomComponentInitializer());
        componentInitializerClassNames
            .add(DefaultComponentInitializer.class.getName());
    }

    public ComponentInitializer create(UIComponent uiComponent)
    {
        String componentKey = uiComponent.getClass().getName();

        if (componentToComponentInitializerMapping.containsKey(componentKey))
        {
            return componentToComponentInitializerMapping.get(componentKey);
        }

        ComponentInitializer componentInitializer;

        for (String componentInitializerName : componentInitializerClassNames)
        {
            componentInitializer =
                (ComponentInitializer) ClassUtils.tryToInstantiateClassForName(componentInitializerName);

            if (componentInitializer != null)
            {
                componentToComponentInitializerMapping.put(componentKey, componentInitializer);

                LogUtils.trace(componentInitializer.getClass().getName() + " used for " + componentKey, getClass());

                return componentInitializer;
            }
        }

        return null;
    }
}