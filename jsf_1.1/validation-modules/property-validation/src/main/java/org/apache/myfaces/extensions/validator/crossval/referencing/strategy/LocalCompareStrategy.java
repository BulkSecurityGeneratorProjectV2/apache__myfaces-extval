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
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * "[property_name]" ... local validation -> cross-component, but no cross-entity validation
 *
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.INTERNAL)
public class LocalCompareStrategy implements ReferencingStrategy
{
    protected final Log logger = LogFactory.getLog(getClass());

    public boolean evalReferenceAndValidate(
            CrossValidationStorageEntry crossValidationStorageEntry,
            CrossValidationStorage crossValidationStorage,
            String validationTarget, AbstractCompareStrategy compareStrategy)
    {
        return tryToValidateLocally(
            crossValidationStorageEntry,
            crossValidationStorage,
            validationTarget,
            compareStrategy);
    }

    protected boolean tryToValidateLocally(
            CrossValidationStorageEntry crossValidationStorageEntry,
            CrossValidationStorage crossValidationStorage,
            String targetKey,
            AbstractCompareStrategy compareStrategy)
    {
        Map<String, ProcessedInformationEntry> keyConvertedValueMapping = CrossValidationUtils
                .getOrInitKeyToConvertedValueMapping();

        String newKey = createTargetKey(crossValidationStorageEntry, targetKey);
        if (!keyConvertedValueMapping.containsKey(newKey))
        {
            return false;
        }

        PropertyDetails propertyDetails = crossValidationStorageEntry.getMetaDataEntry()
                .getProperty(PropertyInformationKeys.PROPERTY_DETAILS, PropertyDetails.class);

        String sourceKey = propertyDetails.getKey();

        if(!sourceKey.contains("."))
        {
            throw new IllegalStateException("source path: " + sourceKey + " invalid");
        }

        targetKey = sourceKey.substring(0, sourceKey.lastIndexOf(".") + 1) + targetKey;

        ProcessedInformationEntry validationTargetEntry = CrossValidationUtils.resolveValidationTargetEntry(
                keyConvertedValueMapping, targetKey, crossValidationStorageEntry);

        if (validationTargetEntry != null)
        {
            CrossValidationUtils
                    .crossValidateCompareStrategy(compareStrategy, crossValidationStorageEntry, validationTargetEntry);
        }
        else
        {
            if(logger.isWarnEnabled())
            {
                logger.warn("couldn't find converted object for " + propertyDetails.getKey());
            }
        }

        return true;
    }

    protected String createTargetKey(CrossValidationStorageEntry crossValidationStorageEntry, String targetKey)
    {
        //no real value binding expression
        //ValueBindingExpression just hepls to replace the property of the key
        //here only dot-notation is allowed -> no problem
        ValueBindingExpression baseExpression =
            new ValueBindingExpression("#{" + crossValidationStorageEntry.getMetaDataEntry()
                .getProperty(PropertyInformationKeys.PROPERTY_DETAILS,
                    PropertyDetails.class).getKey() + "}");

        String result = ValueBindingExpression.replaceOrAddProperty(baseExpression, targetKey)
            .getExpressionString();
        return result.substring(2, result.length() -1);
    }
}
