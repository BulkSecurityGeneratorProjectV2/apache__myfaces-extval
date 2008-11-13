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
package org.apache.myfaces.extensions.validator.baseval.strategy;

import org.apache.myfaces.extensions.validator.core.metadata.CommonMetaDataKeys;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.validation.strategy.AbstractAnnotationValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.AbstractValidationErrorMessageResolver;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.application.FacesMessage;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.util.MissingResourceException;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.INTERNAL)
public class JpaValidationStrategy extends AbstractAnnotationValidationStrategy
{
    private boolean useFacesBundle = false;
    private static final String JAVAX_FACES_REQUIRED = "javax.faces.component.UIInput.REQUIRED";
    private static final String JAVAX_FACES_REQUIRED_DETAIL = "javax.faces.component.UIInput.REQUIRED_detail";
    private static final String JAVAX_FACES_MAXIMUM = "javax.faces.validator.LengthValidator.MAXIMUM";
    private static final String JAVAX_FACES_MAXIMUM_DETAIL = "javax.faces.validator.LengthValidator.MAXIMUM_detail";

    private static final String VALIDATE_LENGTH = "length";

    private String violation;
    private int maxLength;

    public void processValidation(FacesContext facesContext,
                                  UIComponent uiComponent,
                                  MetaDataEntry metaDataEntry,
                                  Object convertedObject) throws ValidatorException
    {
        Annotation annotation = metaDataEntry.getValue(Annotation.class);
        if (annotation instanceof Column)
        {
            validateColumnAnnotation((Column) annotation, convertedObject);
        }
        else if (annotation instanceof Basic)
        {
            validateBasicAnnotation((Basic) annotation, convertedObject);
        }
        else if (annotation instanceof Id)
        {
            checkRequiredConvertedObject(convertedObject);
        }
        else if (annotation instanceof OneToOne)
        {
            validateOneToOneAnnotation((OneToOne) annotation, convertedObject);
        }
        else if (annotation instanceof ManyToOne)
        {
            validateManyToOneAnnotation((ManyToOne) annotation, convertedObject);
        }
    }

    private void validateColumnAnnotation(Column column, Object convertedObject) throws ValidatorException
    {
        if (!column.nullable())
        {
            checkRequiredConvertedObject(convertedObject);
        }

        if (convertedObject == null)
        {
            return;
        }

        if (convertedObject instanceof String
                && column.length() < ((String) convertedObject).length())
        {
            this.violation = VALIDATE_LENGTH;
            this.maxLength = column.length();
            throw new ValidatorException(getValidationErrorFacesMassage(null));
        }
    }

    private void validateBasicAnnotation(Basic basic, Object convertedObject) throws ValidatorException
    {
        if (!basic.optional())
        {
            checkRequiredConvertedObject(convertedObject);
        }
    }

    private void validateOneToOneAnnotation(OneToOne oneToOne, Object convertedObject)
    {
        if (!oneToOne.optional())
        {
            checkRequiredConvertedObject(convertedObject);
        }
    }

    private void validateManyToOneAnnotation(ManyToOne manyToOne, Object convertedObject)
    {
        if (!manyToOne.optional())
        {
            checkRequiredConvertedObject(convertedObject);
        }
    }

    @ToDo(Priority.MEDIUM)
    private void checkRequiredConvertedObject(Object convertedObject) throws ValidatorException
    {
        if (convertedObject == null || convertedObject.equals(""))
        {
            this.violation = CommonMetaDataKeys.REQUIRED;
            throw new ValidatorException(getValidationErrorFacesMassage(null));
        }
    }

    protected String getValidationErrorMsgKey(Annotation annotation)
    {
        if (VALIDATE_LENGTH.equals(this.violation))
        {
            return "field_too_long";
        }
        else
        {
            return "field_required";
        }
    }

    protected String getErrorMessageDetails(Annotation annotation)
    {
        String message = super.getErrorMessageDetails(annotation);

        if (VALIDATE_LENGTH.equals(this.violation))
        {
            return message.replace("{0}", "" + this.maxLength);
        }
        else
        {
            return message;
        }
    }

    @Override
    protected String resolveMessage(String key)
    {
        String result = super.resolveMessage(key);
        String marker = AbstractValidationErrorMessageResolver.MISSING_RESOURCE_MARKER;

        if((marker + key + marker).equals(result))
        {
            this.useFacesBundle = true;
        }

        return result;
    }

    @Override
    protected boolean processAfterValidatorException(FacesContext facesContext,
                                                     UIComponent uiComponent,
                                                     MetaDataEntry metaDataEntry,
                                                     Object convertedObject,
                                                     ValidatorException e)
    {
        FacesMessage facesMessage = e.getFacesMessage();

        if(this.useFacesBundle)
        {
            String facesRequiredMessage;
            String facesRequiredMessageDetail;

            if(VALIDATE_LENGTH.equals(this.violation))
            {
                facesRequiredMessage = getDefaultFacesMessageBundle().getString(JAVAX_FACES_MAXIMUM);
                facesRequiredMessageDetail = facesRequiredMessage;

                //use try/catch for easier sync between trunk/branch
                try
                {
                    if(getDefaultFacesMessageBundle().getString(JAVAX_FACES_MAXIMUM_DETAIL) != null)
                    {
                        facesRequiredMessageDetail = getDefaultFacesMessageBundle()
                                .getString(JAVAX_FACES_MAXIMUM_DETAIL);
                    }
                }
                catch (MissingResourceException missingResourceException)
                {
                    //jsf 1.2 doesn't have a detail message
                }

                facesRequiredMessage = facesRequiredMessage.replace("{0}", "" + this.maxLength);
                facesRequiredMessageDetail = facesRequiredMessageDetail.replace("{0}", "" + this.maxLength);
            }
            else
            {
                facesRequiredMessage = getDefaultFacesMessageBundle().getString(JAVAX_FACES_REQUIRED);
                facesRequiredMessageDetail = facesRequiredMessage;

                //use try/catch for easier sync between trunk/branch
                try
                {
                    if(getDefaultFacesMessageBundle().getString(JAVAX_FACES_REQUIRED_DETAIL) != null)
                    {
                        facesRequiredMessageDetail = getDefaultFacesMessageBundle()
                                .getString(JAVAX_FACES_REQUIRED_DETAIL);
                    }
                }
                catch (MissingResourceException missingResourceException)
                {
                    //jsf 1.2 doesn't have a detail message
                }
            }

            facesMessage.setSummary(facesRequiredMessage);
            facesMessage.setDetail(facesRequiredMessageDetail);
        }

        return super.processAfterValidatorException(facesContext, uiComponent, metaDataEntry, convertedObject, e);
    }
}
