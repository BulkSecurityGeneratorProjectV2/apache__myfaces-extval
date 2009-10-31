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

import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.SkipValidationEvaluator;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.metadata.CommonMetaDataKeys;
import org.apache.myfaces.extensions.validator.core.storage.FacesInformationStorage;
import org.apache.myfaces.extensions.validator.util.PropertyValidationUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.event.PhaseId;
import java.util.List;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@UsageInformation(UsageCategory.INTERNAL)
public class PropertyValidationSkipValidationEvaluator implements SkipValidationEvaluator
{
    public boolean skipValidation(FacesContext facesContext,
                                     UIComponent uiComponent,
                                     ValidationStrategy validationStrategy,
                                     MetaDataEntry metaDataEntry)
    {
        boolean result = false;
        
        if(isRenderResponsePhase())
        {
            result = !isClientSideValidationEnabled(metaDataEntry);
        }
        return result || PropertyValidationUtils.isValidationSkipped(facesContext, validationStrategy, metaDataEntry);
    }

    private boolean isRenderResponsePhase()
    {
        return PhaseId.RENDER_RESPONSE.equals(getFacesInformationStorage().getCurrentPhaseId());
    }

    private FacesInformationStorage getFacesInformationStorage()
    {
        return ExtValUtils.getStorage(FacesInformationStorage.class, FacesInformationStorage.class.getName());
    }

    @SuppressWarnings({"unchecked"})
    private boolean isClientSideValidationEnabled(MetaDataEntry entry)
    {
        List<String> keysToDisable = entry.getProperty(
                                CommonMetaDataKeys.DISABLE_CLIENT_SIDE_VALIDATION, List.class);

        return keysToDisable == null || !keysToDisable.contains(entry.getKey());
    }
}