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
package org.apache.myfaces.extensions.validator.core.interceptor;

import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer;
import org.apache.myfaces.extensions.validator.core.annotation.extractor.AnnotationExtractor;
import org.apache.myfaces.extensions.validator.core.annotation.extractor.AnnotationExtractorFactory;
import org.apache.myfaces.extensions.validator.core.annotation.AnnotationEntry;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.factory.FactoryNames;
import org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.INTERNAL)
public class ValidationInterceptor extends AbstractRendererInterceptor
{
    @Override
    public void beforeEncodeBegin(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped)
        throws IOException
    {
        initComponent(facesContext, uiComponent);
    }

    protected void initComponent(FacesContext facesContext, UIComponent uiComponent)
    {
        if(!(uiComponent instanceof EditableValueHolder))
        {
            return;
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("start to init component " + uiComponent.getClass().getName());
        }

        ValidationStrategy validationStrategy;
        MetaDataTransformer metaDataTransformer;

        AnnotationExtractor annotationExtractor = ExtValContext.getContext().getFactoryFinder().getFactory(
            FactoryNames.COMPONENT_ANNOTATION_EXTRACTOR_FACTORY, AnnotationExtractorFactory.class).create();

        Map<String, Object> metaData;
        for (AnnotationEntry entry : annotationExtractor.extractAnnotations(facesContext, uiComponent))
        {

            validationStrategy = ExtValUtils.getValidationStrategyForAnnotation(entry.getAnnotation());

            if (validationStrategy != null)
            {
                metaDataTransformer = ExtValUtils.getMetaDataTransformerForValidationStrategy(validationStrategy);

                if(metaDataTransformer != null)
                {
                    metaData = metaDataTransformer.extractMetaData(entry.getAnnotation());
                }
                else
                {
                    metaData = null;
                }

                if(metaData == null)
                {
                    metaData = new HashMap<String, Object>();
                }

                //get component initializer for the current component and configure it
                ExtValUtils.configureComponentWithMetaData(facesContext, uiComponent, metaData);
            }
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("init component of " + uiComponent.getClass().getName() + " finished");
        }
    }

    @Override
    public void beforeGetConvertedValue(FacesContext facesContext, UIComponent uiComponent, Object o, Renderer wrapped)
        throws ConverterException
    {
        Object convertedObject = wrapped.getConvertedValue(facesContext, uiComponent, o);

        //recorde user input e.g. for cross-component validation
        for(ProcessedInformationRecorder recorder : ExtValContext.getContext().getProcessedInformationRecorders())
        {
            recorder.recordUserInput(uiComponent, convertedObject);

            if(logger.isTraceEnabled())
            {
                logger.trace(recorder.getClass().getName() + " called");
            }
        }

        processValidation(facesContext, uiComponent, convertedObject);
    }

    protected void processValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject)
    {
        if (!(uiComponent instanceof EditableValueHolder))
        {
            return;
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("start validation");
        }

        ValidationStrategy validationStrategy;

        AnnotationExtractor annotationExtractor = ExtValUtils.getAnnotationExtractor();

        for (AnnotationEntry entry : annotationExtractor.extractAnnotations(facesContext, uiComponent))
        {
            validationStrategy = ExtValUtils.getValidationStrategyForAnnotation(entry.getAnnotation());

            if (validationStrategy != null)
            {
                if(logger.isTraceEnabled())
                {
                    logger.trace("validate " + entry.getAnnotation().getClass().getName() + " with " +
                            validationStrategy.getClass().getName());
                }

                validationStrategy.validate(facesContext, uiComponent, entry, convertedObject);
            }
            else
            {
                if(logger.isTraceEnabled())
                {
                    logger.trace("no validation strategy found for "
                            + entry.getAnnotation().annotationType().getName());
                }
            }
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("validation finished");
        }
    }
}
