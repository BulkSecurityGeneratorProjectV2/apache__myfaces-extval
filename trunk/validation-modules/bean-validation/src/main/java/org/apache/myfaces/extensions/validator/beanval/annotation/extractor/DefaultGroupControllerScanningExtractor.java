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
package org.apache.myfaces.extensions.validator.beanval.annotation.extractor;

import org.apache.myfaces.extensions.validator.core.metadata.extractor.DefaultComponentMetaDataExtractor;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.DefaultPropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;

import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@ToDo(value = Priority.MEDIUM, description = "use meta-data storage - but a special impl.")
public class DefaultGroupControllerScanningExtractor extends DefaultComponentMetaDataExtractor
{
    @Override
    public PropertyInformation extract(FacesContext facesContext, Object object)
    {
        if (!(object instanceof PropertyDetails))
        {
            throw new IllegalStateException(object.getClass() + " is not a " + PropertyDetails.class.getName());
        }

        PropertyDetails propertyDetails = (PropertyDetails)object;

        Class entityClass = propertyDetails.getBaseObject().getClass();

        PropertyInformation propertyInformation = new DefaultPropertyInformation();
        propertyInformation.setInformation(PropertyInformationKeys.PROPERTY_DETAILS, propertyDetails);

        extractAnnotations(propertyInformation, propertyDetails, entityClass);

        return propertyInformation;
    }
}
