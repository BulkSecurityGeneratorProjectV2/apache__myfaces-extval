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
package org.apache.myfaces.extensions.validator.trinidad.validation.message;

import org.apache.myfaces.extensions.validator.core.validation.message.DefaultFacesMessageFactory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

import javax.faces.application.FacesMessage;

/**
 * @since 1.x.2
 */
@UsageInformation(UsageCategory.INTERNAL)
public class TrinidadFacesMessageFactory extends DefaultFacesMessageFactory
{
    @Override
    protected boolean isLabeledFacesMessage(FacesMessage facesMessage)
    {
        return facesMessage instanceof TrinidadViolationMessage;
    }

    @Override
    public FacesMessage create(FacesMessage.Severity severity, String summary, String detail)
    {
        return new TrinidadViolationMessage(severity, summary, detail);
    }
}
