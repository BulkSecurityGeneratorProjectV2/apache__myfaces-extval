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
package org.apache.myfaces.extensions.validator.crossval.recorder;

import org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder;
import org.apache.myfaces.extensions.validator.core.el.TargetInformationEntry;
import org.apache.myfaces.extensions.validator.crossval.ProcessedInformationEntry;
import org.apache.myfaces.extensions.validator.util.CrossValidationUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.INTERNAL)
public class CrossValidationUserInputRecorder implements ProcessedInformationRecorder
{
    public void recordUserInput(UIComponent uiComponent, Object value)
    {
        if (!(uiComponent instanceof EditableValueHolder))
        {
            return;
        }

        //to support local cross-validation (within the same entity)
        Map<String, ProcessedInformationEntry> keyToConvertedValueMapping = CrossValidationUtils
            .getOrInitKeyToConvertedValueMapping();

        ProcessedInformationEntry entry;

        TargetInformationEntry targetInformationEntry =
            ExtValUtils.getELHelper().getTargetInformation(uiComponent);

        if(targetInformationEntry == null)
        {
            return;
        }
        
        entry = new ProcessedInformationEntry();
        entry.setBean(targetInformationEntry.getBaseObject());
        entry.setConvertedValue(value);
        entry.setComponent(uiComponent);
        entry.setClientId(uiComponent.getClientId(FacesContext.getCurrentInstance()));

        String key = targetInformationEntry.getKey();

        //for local cross-validation
        if (keyToConvertedValueMapping.containsKey(key) &&
            keyToConvertedValueMapping.get(key).getBean() != null &&
            !keyToConvertedValueMapping.get(key).getBean().equals(entry.getBean()))
        {
            //for the validation within a complex component e.g. a table
            //don't override existing expression (style: #{entry.property}) - make a special mapping

            List<ProcessedInformationEntry> furtherEntries =
                keyToConvertedValueMapping.get(key).getFurtherEntries();

            if (furtherEntries == null)
            {
                furtherEntries = new ArrayList<ProcessedInformationEntry>();

                keyToConvertedValueMapping.get(key).setFurtherEntries(furtherEntries);
            }

            furtherEntries.add(entry);
        }
        else
        {
            //for normal validation
            keyToConvertedValueMapping.put(key, entry);
        }
    }
}
