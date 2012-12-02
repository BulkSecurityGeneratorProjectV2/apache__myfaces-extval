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
import org.apache.myfaces.extensions.validator.core.mapper.NameMapper;

/**
 * Interface for factories which are using {@link NameMapper}s for creating instances.
 *
 * @since 1.x.2
 */
@UsageInformation(UsageCategory.API)
public interface NameMapperAwareFactory<T extends NameMapper>
{
    /**
     * Registers a new {@link NameMapper} which should be used for the mapping process.
     * It will be used if it isn't denied.
     *
     * @param classToAdd {@link NameMapper} to add
     */
    void register(T classToAdd);

    /**
     * Removes all {@link NameMapper}s of the given type.
     * @param classToDeregister nameMapper to remove.
     */
    void deregister(Class<? extends NameMapper> classToDeregister);

    /**
     * Deregisters existing {@link NameMapper}s of the given type and deny the type for {@link NameMapper} which
     * might be added later on.
     * @param classToDeny {@link NameMapper} to deny
     */
    void deny(Class<? extends NameMapper> classToDeny);
}
