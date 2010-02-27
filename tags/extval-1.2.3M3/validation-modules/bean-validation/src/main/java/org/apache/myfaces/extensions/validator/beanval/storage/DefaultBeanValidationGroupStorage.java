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
package org.apache.myfaces.extensions.validator.beanval.storage;

import org.apache.myfaces.extensions.validator.core.storage.DefaultGroupStorage;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import static org.apache.myfaces.extensions.validator.internal.UsageCategory.INTERNAL;

/**
 * default storage implementation for bean-validation groups
 *
 * @author Gerhard Petracek
 * @since x.x.3
 */
@UsageInformation(INTERNAL)
public class DefaultBeanValidationGroupStorage extends DefaultGroupStorage
{
    @Override
    public Class[] getGroups(String viewId, String clientId)
    {
        Class[] result = super.getGroups(viewId, clientId);

        if(result == null)
        {
            //the default group will be validated automatically
            return new Class[] {};
        }

        return result;
    }
}
