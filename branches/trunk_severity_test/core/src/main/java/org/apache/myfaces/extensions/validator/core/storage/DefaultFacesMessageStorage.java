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
package org.apache.myfaces.extensions.validator.core.storage;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@UsageInformation(UsageCategory.API)
public class DefaultFacesMessageStorage implements FacesMessageStorage
{
    Map<String, ValidationResult> results = new HashMap<String, ValidationResult>();

    public void addFacesMessage(String componentId, FacesMessage facesMessage)
    {
        if(componentId == null)
        {
            componentId = "*";
        }

        if(!this.results.containsKey(componentId))
        {
            this.results.put(componentId, new ValidationResult());
        }

        this.results.get(componentId).addFacesMessageHolder(new FacesMessageHolder(facesMessage, componentId));
    }

    public Map<String, FacesMessage> getFacesMessages()
    {
        Map<String, FacesMessage> result = new HashMap<String, FacesMessage>();

        for(ValidationResult validationResult : this.results.values())
        {
            for(FacesMessageHolder facesMessageHolder : validationResult.getFacesMessageHolderList())
            {
                result.put(facesMessageHolder.getClientId(), facesMessageHolder.getFacesMessage());
            }
        }
        return result;
    }

    public void addAll()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        List<FacesMessageHolder> facesMessageHolderList;

        for(ValidationResult validationResult : this.results.values())
        {
            facesMessageHolderList = validationResult.getFacesMessageHolderList();
            sortFacesMessageHolderList(facesMessageHolderList);

            for(FacesMessageHolder facesMessageHolder : facesMessageHolderList)
            {
                facesContext.addMessage(facesMessageHolder.getClientId(), facesMessageHolder.getFacesMessage());
            }
        }
    }

    @ToDo(Priority.HIGH)
    private void sortFacesMessageHolderList(List<FacesMessageHolder> facesMessageHolderList)
    {
        //sort severities
    }
}