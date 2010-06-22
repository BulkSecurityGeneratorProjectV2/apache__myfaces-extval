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
package org.apache.myfaces.extensions.validator.baseval;

import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.ExtValModuleConfiguration;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

/**
 * @author Gerhard Petracek
 * @since r4
 */
@UsageInformation(UsageCategory.INTERNAL)
public abstract class ExtValBaseValidationModuleConfiguration implements ExtValModuleConfiguration
{
    private static ExtValContext extValContext = ExtValContext.getContext();

    protected ExtValBaseValidationModuleConfiguration()
    {
    }

    public static ExtValBaseValidationModuleConfiguration get()
    {
        return extValContext.getModuleConfiguration(ExtValBaseValidationModuleConfiguration.class);
    }

    public static boolean use(ExtValBaseValidationModuleConfiguration config, boolean forceOverride)
    {
        return extValContext.addModuleConfiguration(
                ExtValBaseValidationModuleConfiguration.class, config, forceOverride);
    }

    /*
     * web.xml config
     */

    public abstract String jpaValidationErrorMessages();

    public abstract boolean deactivateJpaBasedValidation();
}
