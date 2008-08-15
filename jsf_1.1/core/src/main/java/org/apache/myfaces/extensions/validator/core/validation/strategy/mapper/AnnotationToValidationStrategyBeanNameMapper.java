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
package org.apache.myfaces.extensions.validator.core.validation.strategy.mapper;

import org.apache.myfaces.extensions.validator.core.mapper.NameMapper;

import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 *
 * Name Mapper which delegates the name mapping, extract the name and convert it to a bean name + prefix
 * target: configure a validation strategy via a managed bean facility -> allows to inject other beans
 *  instead of api calls + hardcoded bean names
 *
 * allowed bean scopes:
 * the validation strategy is stateless: application/singleton
 * the validation strategy is stateful: none/prototype
 * don't use the session or a conversation scope
 */
public class AnnotationToValidationStrategyBeanNameMapper implements
        NameMapper<Annotation>
{
    public static final String PREFIX_FOR_BEAN_MAPPING = "bean:";
    private NameMapper<Annotation> wrapped;

    public AnnotationToValidationStrategyBeanNameMapper(
            NameMapper<Annotation> nameMapper)
    {
        this.wrapped = nameMapper;
    }

    public String createName(Annotation source)
    {
        String name = wrapped.createName(source);

        if (name == null)
        {
            return null;
        }

        name = name.substring(name.lastIndexOf(".") + 1);
        return PREFIX_FOR_BEAN_MAPPING + name.substring(0, 1).toLowerCase()
                + name.substring(1);
    }
}
