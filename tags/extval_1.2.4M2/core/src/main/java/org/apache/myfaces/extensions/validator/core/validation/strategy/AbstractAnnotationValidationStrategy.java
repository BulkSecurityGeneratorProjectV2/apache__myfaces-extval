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
package org.apache.myfaces.extensions.validator.core.validation.strategy;

import org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.property.PropertyInformationKeys;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.logging.Level;

/**
 * Provides the ability of message resolving to ValidationStrategies
 *
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation({UsageCategory.INTERNAL, UsageCategory.REUSE})
public abstract class AbstractAnnotationValidationStrategy<A extends Annotation> extends AbstractValidationStrategy
{
    protected static final String DETAIL_MESSAGE_KEY_POSTFIX = "_detail";
    //e.g. for injecting a message resolver via spring
    private MessageResolver messageResolver;

    protected String resolveMessage(String key)
    {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

        return this.messageResolver != null ? this.messageResolver.getMessage(key, locale) :
            ExtValUtils.getMessageResolverForValidationStrategy(this).getMessage(key, locale);
    }

    protected String getErrorMessageSummary(A annotation)
    {
        return resolveMessage(getValidationErrorMsgKey(annotation));
    }

    protected String getErrorMessageDetail(A annotation)
    {
        try
        {
            String key = getValidationErrorMsgKey(annotation);
            return (key != null) ? resolveMessage(key + DETAIL_MESSAGE_KEY_POSTFIX) : null;
        }
        catch (MissingResourceException e)
        {
            logger.log(Level.WARNING,
                    "couldn't find key " + getValidationErrorMsgKey(annotation) + DETAIL_MESSAGE_KEY_POSTFIX, e);
        }
        return null;
    }

    protected FacesMessage getValidationErrorFacesMessage(A annotation)
    {
        return ExtValUtils.createFacesMessage(getErrorMessageSummary(annotation), getErrorMessageDetail(annotation));
    }

    protected abstract String getValidationErrorMsgKey(A annotation);

    public void setMessageResolver(MessageResolver messageResolver)
    {
        this.messageResolver = messageResolver;
    }

    @Override
    protected boolean processAfterValidatorException(FacesContext facesContext,
                                                     UIComponent uiComponent,
                                                     MetaDataEntry metaDataEntry,
                                                     Object convertedObject,
                                                     ValidatorException validatorException)
    {
        metaDataEntry.setProperty(PropertyInformationKeys.LABEL, getLabel(facesContext, uiComponent, metaDataEntry));

        return super.processAfterValidatorException(
                facesContext, uiComponent, metaDataEntry, convertedObject, validatorException);
    }

    //e.g. for custom annotations - override if needed
    protected String getLabel(FacesContext facesContext, UIComponent uiComponent, MetaDataEntry metaDataEntry)
    {
        return null;
    }
}
