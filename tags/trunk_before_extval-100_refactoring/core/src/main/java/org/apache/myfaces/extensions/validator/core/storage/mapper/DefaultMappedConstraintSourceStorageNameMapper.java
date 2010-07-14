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
package org.apache.myfaces.extensions.validator.core.storage.mapper;

import org.apache.myfaces.extensions.validator.core.InvocationOrder;
import org.apache.myfaces.extensions.validator.core.mapper.NameMapper;
import org.apache.myfaces.extensions.validator.core.storage.MappedConstraintSourceStorage;
import org.apache.myfaces.extensions.validator.core.storage.DefaultMappedConstraintSourceStorage;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;

/**
 * use a public class to allow optional deregistration
 *
 * @author Gerhard Petracek
 * @since r4
 */
@InvocationOrder(100)
@UsageInformation(UsageCategory.INTERNAL)
public class DefaultMappedConstraintSourceStorageNameMapper implements NameMapper<String>
{
    public String createName(String source)
    {
        return (MappedConstraintSourceStorage.class.getName().equals(source)) ?
                DefaultMappedConstraintSourceStorage.class.getName() : null;
    }
}
