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
package org.apache.myfaces.extensions.validator.beanval.metadata.transformer.mapper;

import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.core.Nested;
import org.apache.myfaces.extensions.validator.core.metadata.transformer.mapper
        .AbstractValidationStrategyToMetaDataTransformerNameMapper;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.beanval.validation.strategy.BeanValidationVirtualValidationStrategy;
import org.apache.myfaces.extensions.validator.beanval.metadata.transformer.NotNullMetaDataTransformer;

import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.constraints.NotNull;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@Nested
@UsageInformation({UsageCategory.INTERNAL})
public class NotNullNameMapper extends AbstractValidationStrategyToMetaDataTransformerNameMapper
{
    public String createName(ValidationStrategy source)
    {
        if(source instanceof BeanValidationVirtualValidationStrategy)
        {
            BeanValidationVirtualValidationStrategy beanValidationAdapter =
                    (BeanValidationVirtualValidationStrategy)source;

            ConstraintDescriptor descriptor = beanValidationAdapter.getConstraintDescriptor();

            if(NotNull.class.getName().equals(descriptor.getAnnotation().annotationType().getName()))
            {
                return NotNullMetaDataTransformer.class.getName();
            }
        }
        return null;
    }
}
