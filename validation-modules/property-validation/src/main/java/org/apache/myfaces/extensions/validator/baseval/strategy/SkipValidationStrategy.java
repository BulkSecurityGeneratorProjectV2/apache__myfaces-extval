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
package org.apache.myfaces.extensions.validator.baseval.strategy;

import org.apache.myfaces.extensions.validator.baseval.annotation.SkipValidation;
import org.apache.myfaces.extensions.validator.core.el.ELHelper;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.NullValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.EmptyValueAwareValidationStrategy;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 * @since 1.x.1
 */
@NullValueAwareValidationStrategy
@EmptyValueAwareValidationStrategy
@UsageInformation(UsageCategory.INTERNAL)
public class SkipValidationStrategy implements ValidationStrategy
{
    public void validate(FacesContext facesContext,
                                  UIComponent uiComponent,
                                  MetaDataEntry metaDataEntry,
                                  Object convertedObject) throws ValidatorException
    {
        String[] valueBindingExpressions = metaDataEntry.getValue(SkipValidation.class).value();

        ELHelper elHelper = ExtValUtils.getELHelper();
        for(String valueBindingExpression : valueBindingExpressions)
        {
            if(Boolean.TRUE.equals(elHelper.getValueOfExpression(
                facesContext, new ValueBindingExpression(valueBindingExpression))))
            {
                metaDataEntry.setProperty(PropertyInformationKeys.SKIP_VALIDATION, true);
                break;
            }
        }
    }
}
