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
package org.apache.myfaces.extensions.validator.beanval.validation;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.core.validation.message.FacesMessageHolder;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@ToDo(value = Priority.LOW, description = "refactor")
@UsageInformation(UsageCategory.INTERNAL)
class ModelValidationResult
{
    private List<FacesMessageHolder> facesMessageHolderList = new ArrayList<FacesMessageHolder>();

    public void addFacesMessageHolder(FacesMessageHolder facesMessageHolder)
    {
        this.facesMessageHolderList.add(facesMessageHolder);
    }

    public List<FacesMessageHolder> getFacesMessageHolderList()
    {
        return facesMessageHolderList;
    }
}
