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
package org.apache.myfaces.extensions.validator.core.metadata.extractor;

import org.apache.myfaces.extensions.validator.core.mapper.ClassMappingFactory;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.strategy.BeanValidationStrategyAdapter;
import org.apache.myfaces.extensions.validator.core.metadata.extractor.mapper
    .CustomConfiguredValidationStrategyToMetaDataExtractorNameMapper;
import org.apache.myfaces.extensions.validator.core.metadata.extractor.mapper
    .CustomConventionValidationStrategyToMetaDataExtractorNameMapper;
import org.apache.myfaces.extensions.validator.core.metadata.extractor.mapper
    .DefaultValidationStrategyToMetaDataExtractorNameMapper;
import org.apache.myfaces.extensions.validator.core.metadata.extractor.mapper
    .SimpleValidationStrategyToMetaDataExtractorNameMapper;
import org.apache.myfaces.extensions.validator.core.metadata.extractor.mapper
    .BeanValidationStrategyToMetaDataExtractorNameMapper;
import org.apache.myfaces.extensions.validator.core.mapper.NameMapper;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Factory which creates the MetaDataExtractor for a given ValidationStrategy
 *
 * @author Gerhard Petracek
 * @since 1.x.1
 */
/*
 * ValidationStrategy -> MetaDataExtractor instead of Annotation -> MetaDataExtractor
 * to avoid a second static mapping e.g. for jpa annotations
 */
@UsageInformation({UsageCategory.INTERNAL, UsageCategory.CUSTOMIZABLE})
public class DefaultMetaDataExtractorFactory implements
    ClassMappingFactory<ValidationStrategy, MetaDataExtractor>
{
    private static Map<String, String> validationStrategyToMetaDataExtractorMapping = new HashMap<String, String>();
    private static List<NameMapper<ValidationStrategy>> nameMapperList
        = new ArrayList<NameMapper<ValidationStrategy>>();

    static
    {
        nameMapperList
            .add(new CustomConfiguredValidationStrategyToMetaDataExtractorNameMapper());
        nameMapperList
            .add(new CustomConventionValidationStrategyToMetaDataExtractorNameMapper());
        nameMapperList
            .add(new DefaultValidationStrategyToMetaDataExtractorNameMapper());
        nameMapperList
            .add(new SimpleValidationStrategyToMetaDataExtractorNameMapper());
        nameMapperList
            .add(new BeanValidationStrategyToMetaDataExtractorNameMapper());
    }

    public MetaDataExtractor create(ValidationStrategy validationStrategy)
    {
        String validationStrategyName = null;

        //proxy check
        if(validationStrategy.getClass().getPackage() != null)
        {
            validationStrategyName = validationStrategy.getClass().getName();
        }
        //in case of a proxy and the usage of a BeanValidationStrategyAdapter
        else if (validationStrategy instanceof BeanValidationStrategyAdapter)
        {
            validationStrategyName = ((BeanValidationStrategyAdapter)validationStrategy)
                                        .getValidationStrategyClassName();
        }

        if (validationStrategyToMetaDataExtractorMapping.containsKey(validationStrategyName))
        {
            return (MetaDataExtractor)ClassUtils.tryToInstantiateClassForName(
                validationStrategyToMetaDataExtractorMapping.get(validationStrategyName));
        }

        MetaDataExtractor metaDataExtractor;
        String extractorName;
        //null -> use name mappers
        for (NameMapper<ValidationStrategy> nameMapper : nameMapperList)
        {
            extractorName = nameMapper.createName(validationStrategy);

            if (extractorName == null)
            {
                continue;
            }

            metaDataExtractor = (MetaDataExtractor)ClassUtils.tryToInstantiateClassForName(extractorName);

            if (metaDataExtractor != null)
            {
                if(validationStrategyName != null)
                {
                    validationStrategyToMetaDataExtractorMapping.put(validationStrategyName, extractorName);
                }
                return metaDataExtractor;
            }
        }

        return null;
    }
}
