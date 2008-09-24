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

import org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataKeys;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
public class HtmlCoreComponentsComponentInitializer implements ComponentInitializer
{
    public void configureComponent(FacesContext facesContext, UIComponent uiComponent, Map<String, Object> metaData)
    {
        configureRequiredAttribute(facesContext, uiComponent, metaData);
        configureMaxLengthAttribute(facesContext, uiComponent, metaData);
    }

    protected void configureRequiredAttribute(FacesContext facesContext,
                                              UIComponent uiComponent,
                                              Map<String, Object> metaData)
    {
        if(metaData.containsKey(MetaDataKeys.REQUIRED))
        {
            if((Boolean)metaData.get(MetaDataKeys.REQUIRED) && Boolean.TRUE.equals(isComponentRequired(uiComponent)))
            {
                ((EditableValueHolder)uiComponent).setRequired(true);
            }
        }
    }

    /**
     * if there is no special attribute at the component which should overrule
     * the annotated property return true!
     *
     * @param uiComponent component which implements the EditableValueHolder interface
     * @return false to overrule the annotated property e.g. if component is readonly
     */
    protected Boolean isComponentRequired(UIComponent uiComponent)
    {
        if(uiComponent instanceof HtmlInputText)
        {
            HtmlInputText htmlInputText = (HtmlInputText)uiComponent;
            return !(htmlInputText.isReadonly() || htmlInputText.isDisabled());
        }

        return null;
    }

    protected void configureMaxLengthAttribute(FacesContext facesContext,
                                             UIComponent uiComponent,
                                             Map<String, Object> metaData)
    {
        if(metaData.containsKey(MetaDataKeys.MAX_LENGTH))
        {
            Object maxLength = metaData.get(MetaDataKeys.MAX_LENGTH);

            if(!(maxLength instanceof Integer))
            {
                return;
            }
            if(uiComponent instanceof HtmlInputText)
            {
                HtmlInputText htmlInputText = (HtmlInputText)uiComponent;
                htmlInputText.setMaxlength((Integer)maxLength);
            }
        }
    }
}
