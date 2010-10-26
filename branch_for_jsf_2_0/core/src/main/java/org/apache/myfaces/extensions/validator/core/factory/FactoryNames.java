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
package org.apache.myfaces.extensions.validator.core.factory;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

/**
 * Names of factories known by the
 * {@link org.apache.myfaces.extensions.validator.core.factory.FactoryFinder}
 * 
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation({UsageCategory.API})
public enum FactoryNames
{
    COMPONENT_META_DATA_EXTRACTOR_FACTORY,

    VALIDATION_PARAMETER_EXTRACTOR_FACTORY,
    VALIDATION_PARAMETER_FACTORY,

    VALIDATION_STRATEGY_FACTORY,
    MESSAGE_RESOLVER_FACTORY,
    META_DATA_TRANSFORMER_FACTORY,

    FACES_MESSAGE_FACTORY,

    RENDERKIT_WRAPPER_FACTORY,
    EL_HELPER_FACTORY,

    STORAGE_MANAGER_FACTORY
}
