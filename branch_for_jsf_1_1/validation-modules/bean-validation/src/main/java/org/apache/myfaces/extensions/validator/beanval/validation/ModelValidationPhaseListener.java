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
package org.apache.myfaces.extensions.validator.beanval.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.extensions.validator.beanval.ExtValBeanValidationContext;
import org.apache.myfaces.extensions.validator.beanval.BeanValidationModuleKey;
import org.apache.myfaces.extensions.validator.beanval.annotation.ModelValidation;
import org.apache.myfaces.extensions.validator.beanval.storage.ModelValidationEntry;
import org.apache.myfaces.extensions.validator.beanval.util.BeanValidationUtils;
import org.apache.myfaces.extensions.validator.core.validation.message.FacesMessageHolder;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.DefaultPropertyInformation;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.core.property.PropertyDetails;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class ModelValidationPhaseListener implements PhaseListener
{
    private static final long serialVersionUID = -3482233893186708878L;

    protected final Log logger = LogFactory.getLog(getClass());

    public void afterPhase(PhaseEvent phaseEvent)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("jsr303 start model validation");
        }

        List<Object> processedValidationTargets = new ArrayList<Object>();

        Map<String, ModelValidationResult> results = new HashMap<String, ModelValidationResult>();

        for (ModelValidationEntry modelValidationEntry : getModelValidationEntriesToValidate())
        {
            processModelValidation(modelValidationEntry, processedValidationTargets, results);
        }

        processViolations(results);

        executeGlobalAfterValidationInterceptorsFor(results);

        if (logger.isTraceEnabled())
        {
            logger.trace("jsr303 validation finished");
        }
    }

    private List<ModelValidationEntry> getModelValidationEntriesToValidate()
    {
        return ExtValBeanValidationContext.getCurrentInstance().getModelValidationEntriesToValidate();
    }

    private void processModelValidation(ModelValidationEntry modelValidationEntry,
                                        List<Object> processedValidationTargets,
                                        Map<String, ModelValidationResult> results)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        PropertyInformation propertyInformation;

        for (Object validationTarget : modelValidationEntry.getValidationTargets())
        {
            propertyInformation = createPropertyInformation(modelValidationEntry, validationTarget);

            if(!executeGlobalBeforeValidationInterceptors(
                    facesContext, modelValidationEntry.getComponent(), validationTarget, propertyInformation))
            {
                return;
            }

            if (processedValidationTargets.contains(validationTarget) &&
                    !modelValidationEntry.isDisplayMessageInline())
            {
                continue;
            }

            if (!processedValidationTargets.contains(validationTarget))
            {
                processedValidationTargets.add(validationTarget);
            }

            validateTarget(modelValidationEntry, validationTarget, modelValidationEntry.getGroups(), results);
        }
    }

    private PropertyInformation createPropertyInformation(
            ModelValidationEntry modelValidationEntry, Object validationTarget)
    {
        PropertyInformation propertyInformation;
        PropertyDetails propertyDetails;
        propertyInformation = new DefaultPropertyInformation();
        if(modelValidationEntry.getComponent() != null)
        {
            propertyDetails = ExtValUtils.getELHelper()
                    .getPropertyDetailsOfValueBinding(modelValidationEntry.getComponent());
        }
        else
        {
            propertyDetails = new PropertyDetails(null, validationTarget, null);
        }
        propertyInformation.setInformation(PropertyInformationKeys.PROPERTY_DETAILS, propertyDetails);
        return propertyInformation;
    }

    private boolean executeGlobalBeforeValidationInterceptors(FacesContext facesContext,
                                                              UIComponent uiComponent,
                                                              Object validationTarget,
                                                              PropertyInformation propertyInformation)
    {
        return ExtValUtils.executeGlobalBeforeValidationInterceptors(facesContext, uiComponent, validationTarget,
                PropertyInformation.class.getName(), propertyInformation, BeanValidationModuleKey.class);
    }

    private void executeGlobalAfterValidationInterceptors(FacesContext facesContext,
                                                          UIComponent uiComponent,
                                                          Object validationTarget,
                                                          PropertyInformation propertyInformation)
    {
        ExtValUtils.executeGlobalAfterValidationInterceptors(facesContext, uiComponent, validationTarget,
                PropertyInformation.class.getName(), propertyInformation, BeanValidationModuleKey.class);
    }
    
    private void validateTarget(ModelValidationEntry modelValidationEntry,
                                Object validationTarget,
                                Class[] groups,
                                Map<String, ModelValidationResult> results)
    {
        Set<ConstraintViolation<Object>> violations = ExtValBeanValidationContext.getCurrentInstance()
                .getValidatorFactory().usingContext()
                .messageInterpolator(ExtValBeanValidationContext.getCurrentInstance().getMessageInterpolator())
                .getValidator()
                .validate(validationTarget, groups);

        if (violations != null && !violations.isEmpty())
        {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            //jsf 2.0 is able to display multiple messages per component - so process all violations
            //jsf < 2.0 will just use the first one (it's only a little overhead)
            Iterator violationsIterator = violations.iterator();
            ConstraintViolation constraintViolation;
            ModelValidationResult result;
            while (violationsIterator.hasNext())
            {
                if (!results.containsKey(modelValidationEntry.getComponent().getClientId(facesContext)))
                {
                    result = new ModelValidationResult();
                    results.put(modelValidationEntry.getComponent().getClientId(facesContext), result);
                }

                result = results.get(modelValidationEntry.getComponent().getClientId(facesContext));

                constraintViolation = (ConstraintViolation) violationsIterator.next();
                if (modelValidationEntry.isDisplayMessageInline())
                {
                    result.addFacesMessageHolder(createFacesMessageHolderForConstraintViolation(
                            constraintViolation, modelValidationEntry, validationTarget, true));
                }
                else
                {
                    result.addFacesMessageHolder(createFacesMessageHolderForConstraintViolation(
                            constraintViolation, modelValidationEntry, validationTarget, false));
                }
            }
        }
    }

    private FacesMessageHolder createFacesMessageHolderForConstraintViolation(final ConstraintViolation violation,
                                                                        ModelValidationEntry modelValidationEntry,
                                                                        final Object validationTarget,
                                                                        boolean displayAtComponent)
    {
        final String newViolationMessage = tryToChangeViolationMessage(
                modelValidationEntry, validationTarget, violation);

        ConstraintViolation newConstraintViolation = new ConstraintViolation()
        {
            private ConstraintViolation wrapped = violation;

            public String getMessage()
            {
                return newViolationMessage;
            }

            public String getMessageTemplate()
            {
                return wrapped.getMessageTemplate();
            }

            public Object getRootBean()
            {
                return wrapped.getRootBean();
            }

            public Class getRootBeanClass()
            {
                return wrapped.getRootBeanClass();
            }

            public Object getLeafBean()
            {
                return wrapped.getLeafBean();
            }

            public Path getPropertyPath()
            {
                return wrapped.getPropertyPath();
            }

            public Object getInvalidValue()
            {
                return wrapped.getInvalidValue();
            }

            public ConstraintDescriptor getConstraintDescriptor()
            {
                return wrapped.getConstraintDescriptor();
            }
        };


        UIComponent uiComponent = null;
        String clientId = null;

        if (displayAtComponent)
        {
            uiComponent = modelValidationEntry.getComponent();
            clientId = uiComponent.getClientId(FacesContext.getCurrentInstance());
        }

        FacesMessageHolder result = new FacesMessageHolder(BeanValidationUtils
                .createFacesMessageForConstraintViolation(uiComponent, validationTarget, newConstraintViolation));
        result.setClientId(clientId);
        return result;
    }

    private String tryToChangeViolationMessage(ModelValidationEntry modelValidationEntry,
                                               Object validationTarget,
                                               ConstraintViolation violation)
    {
        if (!isDefaultMessage(modelValidationEntry))
        {
            return interpolateValidationErrorMessage(
                    modelValidationEntry.getCustomMessage(), validationTarget, violation);
        }
        return violation.getMessage();
    }

    private boolean isDefaultMessage(ModelValidationEntry modelValidationEntry)
    {
        return ModelValidation.DEFAULT_MESSAGE.equals(modelValidationEntry.getCustomMessage());
    }

    private String interpolateValidationErrorMessage(String extValInlineMessage,
                                                     final Object validationTarget, final ConstraintViolation violation)
    {
        return ExtValBeanValidationContext.getCurrentInstance().getMessageInterpolator()
                .interpolate(
                        extValInlineMessage,
                        new MessageInterpolator.Context()
                        {
                            public ConstraintDescriptor<?> getConstraintDescriptor()
                            {
                                return violation.getConstraintDescriptor();
                            }

                            public Object getValidatedValue()
                            {
                                return validationTarget;
                            }
                        }
                );
    }

    private void processViolations(Map<String, ModelValidationResult> results)
    {
        for (ModelValidationResult result : results.values())
        {
            BeanValidationUtils.processViolationMessages(result.getFacesMessageHolderList(), false);
        }
    }

    private void executeGlobalAfterValidationInterceptorsFor(Map<String, ModelValidationResult> results)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent component;
        for (ModelValidationResult result : results.values())
        {
            for(FacesMessageHolder facesMessageHolder : result.getFacesMessageHolderList())
            {
                component = null;
                if(facesMessageHolder.getClientId() != null && !facesMessageHolder.getClientId().equals("*"))
                {
                    component = facesContext.getViewRoot().findComponent(facesMessageHolder.getClientId());
                }
                executeGlobalAfterValidationInterceptors(facesContext, component, null, null);
            }
        }
    }

    public void beforePhase(PhaseEvent phaseEvent)
    {
        //do nothing
    }

    public PhaseId getPhaseId()
    {
        return PhaseId.UPDATE_MODEL_VALUES;
    }
}
