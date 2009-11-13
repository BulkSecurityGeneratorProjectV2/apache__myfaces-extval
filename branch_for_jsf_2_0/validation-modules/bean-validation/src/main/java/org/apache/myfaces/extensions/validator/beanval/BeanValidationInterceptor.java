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
package org.apache.myfaces.extensions.validator.beanval;

import org.apache.myfaces.extensions.validator.core.interceptor.AbstractValidationInterceptor;
import org.apache.myfaces.extensions.validator.core.metadata.extractor.MetaDataExtractor;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipBeforeInterceptorsException;
import org.apache.myfaces.extensions.validator.core.renderkit.exception.SkipRendererDelegationException;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.beanval.util.BeanValidationUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Set;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@ToDo(value = Priority.HIGH, description = "sync jsf 2.0 specific changes with bv-branch")
@UsageInformation(UsageCategory.INTERNAL)
public class BeanValidationInterceptor extends AbstractValidationInterceptor
{
    private BeanValidationInterceptorInternals bviUtils = new BeanValidationInterceptorInternals(this.logger);

    @Override
    public void beforeEncodeBegin(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped)
            throws IOException, SkipBeforeInterceptorsException, SkipRendererDelegationException
    {
        if (processComponent(uiComponent))
        {
            initComponent(facesContext, uiComponent);
        }
    }

    protected void initComponent(FacesContext facesContext, UIComponent uiComponent)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("start to init component " + uiComponent.getClass().getName());
        }

        PropertyDetails propertyDetails = bviUtils.extractPropertyDetails(facesContext, uiComponent);

        if (propertyDetails != null)
        {
            bviUtils.initComponentWithPropertyDetails(facesContext, uiComponent, propertyDetails);
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("init component of " + uiComponent.getClass().getName() + " finished");
        }
    }

    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject)
    {
        PropertyInformation propertyInformation = getPropertyInformation(facesContext, uiComponent);

        boolean validateProperty = hasBeanValidationConstraints(propertyInformation);
        try
        {
            if (validateProperty)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("jsr303 start validation");
                }

                if (!executeGlobalBeforeValidationInterceptors(
                        facesContext, uiComponent, convertedObject, propertyInformation))
                {
                    return;
                }

                processFieldValidation(facesContext, uiComponent, convertedObject, propertyInformation);
            }
        }
        finally
        {
            if (validateProperty)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("jsr303 validation finished");
                }

                executeGlobalAfterValidationInterceptors(
                        facesContext, uiComponent, convertedObject, propertyInformation);
            }
        }
    }

    protected PropertyInformation getPropertyInformation(FacesContext facesContext, UIComponent uiComponent)
    {
        MetaDataExtractor metaDataExtractor = bviUtils.getComponentMetaDataExtractor(uiComponent);

        return metaDataExtractor.extract(facesContext, uiComponent);
    }

    protected boolean hasBeanValidationConstraints(PropertyInformation propertyInformation)
    {
        return this.bviUtils.hasBeanValidationConstraints(propertyInformation);
    }

    protected void processFieldValidation(FacesContext facesContext,
                                          UIComponent uiComponent,
                                          Object convertedObject,
                                          PropertyInformation propertyInformation)
    {
        /*not used yet supportMultipleViolationsPerField()*/
        Set<ConstraintViolation> violations = this.bviUtils
                .validate(facesContext, uiComponent, convertedObject, propertyInformation);

        if(violations != null && !violations.isEmpty())
        {
            BeanValidationUtils.processConstraintViolations(facesContext, uiComponent, convertedObject, violations);
        }
    }

    /*
     * e.g. extract groups for validation
     */
    private boolean executeGlobalBeforeValidationInterceptors(FacesContext facesContext,
                                                              UIComponent uiComponent,
                                                              Object convertedObject,
                                                              PropertyInformation propertyInformation)
    {
        return ExtValUtils.executeGlobalBeforeValidationInterceptors(facesContext, uiComponent, convertedObject,
                PropertyInformation.class.getName(), propertyInformation, BeanValidationModuleKey.class);
    }

    private void executeGlobalAfterValidationInterceptors(FacesContext facesContext,
                                                          UIComponent uiComponent,
                                                          Object convertedObject,
                                                          PropertyInformation propertyInformation)
    {
        ExtValUtils.executeGlobalAfterValidationInterceptors(facesContext, uiComponent, convertedObject,
                PropertyInformation.class.getName(), propertyInformation, BeanValidationModuleKey.class);
    }
}
