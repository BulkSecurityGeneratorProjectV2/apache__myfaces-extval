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
package org.apache.myfaces.extensions.validator.baseval.metadata.transformer;

import org.apache.myfaces.extensions.validator.baseval.annotation.LongRange;
import org.apache.myfaces.extensions.validator.core.metadata.CommonMetaDataKeys;
import org.apache.myfaces.extensions.validator.core.metadata.transformer.AbstractMetaDataTransformer;
import org.apache.myfaces.extensions.validator.core.annotation.AnnotationEntry;

import java.util.HashMap;
import java.util.Map;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
public class LongRangeMetaDataTransformer  extends AbstractMetaDataTransformer
{
    protected Map<String, Object> convert(AnnotationEntry annotationEntry)
    {
        Map<String, Object> results = new HashMap<String, Object>();
        Annotation annotation = annotationEntry.getAnnotation();

        long minimum = ((LongRange)annotation).minimum();

        results.put(CommonMetaDataKeys.RANGE_MIN, minimum);
        results.put(CommonMetaDataKeys.RANGE_MAX, ((LongRange)annotation).maximum());

        if(minimum > 0)
        {
            results.put(CommonMetaDataKeys.REQUIRED, true);
        }

        return results;
    }
}