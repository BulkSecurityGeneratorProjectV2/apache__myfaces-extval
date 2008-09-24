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
package org.apache.myfaces.extensions.validator.component.initializer.trinidad;

import org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
public class TrinidadComponentInitializer implements ComponentInitializer
{
    private static final String TRINIDAD_CORE_INPUT_TEXT
                                         = "org.apache.myfaces.trinidad.component.core.input.CoreInputText";
    private static final String TRINIDAD_CORE_INPUT_DATE
                                         = "org.apache.myfaces.trinidad.component.core.input.CoreInputDate";

    private static List<ComponentInitializer> componentInitializers = new ArrayList<ComponentInitializer>();

    static
    {
        componentInitializers.add(new RequiredInitializer());
        componentInitializers.add(new LengthInitializer());
    }

    public void configureComponent(FacesContext facesContext, UIComponent uiComponent, Map<String, Object> metaData)
    {
        for(ComponentInitializer componentInitializer : componentInitializers)
        {
            componentInitializer.configureComponent(facesContext, uiComponent, metaData);
        }
    }

    protected boolean processComponent(UIComponent uiComponent)
    {
        return TRINIDAD_CORE_INPUT_TEXT.equals(uiComponent.getClass().getName()) ||
               TRINIDAD_CORE_INPUT_DATE.equals(uiComponent.getClass().getName());
    }
}