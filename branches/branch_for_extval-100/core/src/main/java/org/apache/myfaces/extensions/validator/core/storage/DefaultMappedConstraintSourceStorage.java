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
package org.apache.myfaces.extensions.validator.core.storage;

import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import static org.apache.myfaces.extensions.validator.internal.UsageCategory.INTERNAL;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ProxyUtils;
import org.apache.myfaces.extensions.validator.util.NullValueAwareConcurrentHashMap;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 * @since r4
 */
@UsageInformation(INTERNAL)
public class DefaultMappedConstraintSourceStorage implements MappedConstraintSourceStorage
{
    protected final Logger logger = Logger.getLogger(getClass().getName());

    private Map<String, Map<String, PropertyDetails>> propertyDetailsMap =
            new ConcurrentHashMap<String, Map<String, PropertyDetails>>();

    public void storeMapping(Class originalClass, String originalProperty, PropertyDetails targetPropertyDetails)
    {
        if(targetPropertyDetails == null)
        {
            if(isFilteredClass(originalClass))
            {
                return;
            }

            Map<String, PropertyDetails> classMap = getMapForClass(originalClass);
            classMap.put(originalProperty, null);
            return;
        }

        PropertyDetails propertyDetails = new PropertyDetails(targetPropertyDetails.getKey(),
                targetPropertyDetails.getBaseObject(), targetPropertyDetails.getProperty());

        getMapForClass(originalClass).put(originalProperty, propertyDetails);
    }

    protected boolean isFilteredClass(Class originalClass)
    {
        return ResourceBundle.class.isAssignableFrom(originalClass);
    }

    public PropertyDetails getMappedConstraintSource(Class originalClass, String originalProperty)
    {
        if(isFilteredClass(originalClass))
        {
            return null;
        }

        PropertyDetails foundEntry = getMapForClass(originalClass).get(originalProperty);

        if(foundEntry == null)
        {
            return null;
        }

        return new PropertyDetails(foundEntry.getKey(), foundEntry.getBaseObject(), foundEntry.getProperty());
    }

    public boolean containsMapping(Class originalClass, String originalProperty)
    {
        if(isFilteredClass(originalClass))
        {
            //avoid scanning process
            return true;
        }
        return getMapForClass(originalClass).containsKey(originalProperty);
    }

    private Map<String, PropertyDetails> getMapForClass(Class target)
    {
        String key = ProxyUtils.getClassName(target);
        if(!this.propertyDetailsMap.containsKey(key))
        {
            this.propertyDetailsMap
                    .put(key, new NullValueAwareConcurrentHashMap<String, PropertyDetails>(createDefaultValue()));
        }
        return this.propertyDetailsMap.get(key);
    }

    private PropertyDetails createDefaultValue()
    {
        return new PropertyDetails(null, null, null);
    }
}
