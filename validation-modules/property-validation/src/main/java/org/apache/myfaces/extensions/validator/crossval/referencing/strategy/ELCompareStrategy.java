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
package org.apache.myfaces.extensions.validator.crossval.referencing.strategy;

import org.apache.myfaces.extensions.validator.crossval.ProcessedInformationEntry;
import org.apache.myfaces.extensions.validator.crossval.CrossValidationStorage;
import org.apache.myfaces.extensions.validator.crossval.CrossValidationStorageEntry;
import org.apache.myfaces.extensions.validator.crossval.strategy.AbstractCompareStrategy;
import org.apache.myfaces.extensions.validator.util.CrossValidationUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;

import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * referencing validation targets - possible formats:
 * "#{[bean_name].[property_name]}" ... cross-entity validation with value binding
 *
 * @author Gerhard Petracek
 */
public class ELCompareStrategy implements ReferencingStrategy
{
    public boolean evalReferenceAndValidate(
            CrossValidationStorageEntry crossValidationStorageEntry,
            CrossValidationStorage crossValidationStorage,
            String validationTarget, AbstractCompareStrategy compareStrategy)
    {
        if (validationTarget.startsWith("#{") && validationTarget.endsWith("}")
                && validateValueBindingFormat(validationTarget))
        {
            tryToValidateValueBinding(crossValidationStorageEntry,
                    validationTarget, crossValidationStorage, compareStrategy);
            return true;
        }
        return false;
    }

    @ToDo(value = Priority.MEDIUM, description = "test")
    protected boolean tryToValidateValueBinding(
            CrossValidationStorageEntry crossValidationStorageEntry,
            String validationTarget,
            CrossValidationStorage crossValidationStorage,
            AbstractCompareStrategy compareStrategy)
    {
        boolean violationFound = false;

        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (!ExtValUtils.getELHelper().isExpressionValid(facesContext, validationTarget))
        {
            return false;
        }

        if (compareStrategy.isViolation(crossValidationStorageEntry
                .getConvertedObject(), ExtValUtils.getELHelper().getValueOfExpression(
                facesContext, validationTarget), crossValidationStorageEntry
                .getAnnotationEntry().getAnnotation()))
        {

            ProcessedInformationEntry validationTargetEntry;
            Map<String, ProcessedInformationEntry> valueBindingConvertedValueMapping = CrossValidationUtils
                    .getOrInitValueBindingConvertedValueMapping();

            validationTargetEntry = valueBindingConvertedValueMapping
                    .get(validationTarget);

            CrossValidationStorageEntry tmpCrossValidationStorageEntry = null;

            if (validationTargetEntry != null)
            {
                tmpCrossValidationStorageEntry = new CrossValidationStorageEntry();
                //TODO test
                if (compareStrategy
                        .useTargetComponentToDisplayErrorMsg(crossValidationStorageEntry))
                {
                    tmpCrossValidationStorageEntry
                            .setComponent(validationTargetEntry.getComponent());
                }
                else
                {
                    tmpCrossValidationStorageEntry
                            .setComponent(crossValidationStorageEntry
                                    .getComponent());
                }
                tmpCrossValidationStorageEntry
                        .setConvertedObject(validationTargetEntry
                                .getConvertedValue());
                tmpCrossValidationStorageEntry
                        .setValidationStrategy(compareStrategy);
            }

            compareStrategy
                    .processTargetComponentAfterViolation(
                            crossValidationStorageEntry,
                            tmpCrossValidationStorageEntry);

            violationFound = true;
        }

        if (violationFound)
        {
            compareStrategy
                    .processSourceComponentAfterViolation(crossValidationStorageEntry);
        }

        return true;
    }

    protected boolean validateValueBindingFormat(String targetProperty)
    {
        int bindingStartIndex = targetProperty.indexOf("#{");
        int bindingEndIndex = targetProperty.indexOf("}");
        int separatorIndex = targetProperty.indexOf(".");

        return (bindingStartIndex > -1 && bindingEndIndex > -1
                && separatorIndex > -1 && bindingStartIndex < bindingEndIndex && bindingEndIndex > separatorIndex);
    }
}