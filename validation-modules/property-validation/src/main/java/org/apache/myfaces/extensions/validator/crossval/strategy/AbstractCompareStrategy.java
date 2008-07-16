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
package org.apache.myfaces.extensions.validator.crossval.strategy;

import org.apache.myfaces.extensions.validator.core.ProcessedInformationEntry;
import org.apache.myfaces.extensions.validator.crossval.CrossValidationStorage;
import org.apache.myfaces.extensions.validator.crossval.CrossValidationStorageEntry;
import org.apache.myfaces.extensions.validator.crossval.referencing.strategy.AbstractAliasCompareStrategy;
import org.apache.myfaces.extensions.validator.crossval.referencing.strategy.AbstractELCompareStrategy;
import org.apache.myfaces.extensions.validator.crossval.referencing.strategy.AbstractLocalCompareStrategy;
import org.apache.myfaces.extensions.validator.crossval.referencing.strategy.ReferencingStrategy;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author Gerhard Petracek
 */
public abstract class AbstractCompareStrategy extends AbstractCrossValidationStrategy {
    protected static List<ReferencingStrategy> referencingStrategies;
    protected Map<Object, Object> violationResultStorage = new HashMap<Object, Object>();

    public AbstractCompareStrategy() {
        initReferencingStrategies();
    }

    protected void initReferencingStrategies() {
        if (referencingStrategies == null) {
            referencingStrategies = new ArrayList<ReferencingStrategy>();

            String customReferencingStrategyClassName = ExtValUtils.getBasePackage() + "ReferencingStrategy";
            ReferencingStrategy customReferencingStrategy = (ReferencingStrategy) ClassUtils.tryToInstantiateClassForName(customReferencingStrategyClassName);

            if (customReferencingStrategy != null) {
                referencingStrategies.add(customReferencingStrategy);
            }

            referencingStrategies.add(new AbstractELCompareStrategy());
            referencingStrategies.add(new AbstractAliasCompareStrategy());
            referencingStrategies.add(new AbstractLocalCompareStrategy());
        }
    }

    public void processCrossValidation(CrossValidationStorageEntry crossValidationStorageEntry, CrossValidationStorage crossValidationStorage) throws ValidatorException {

        String[] validationTargets = getValidationTargets(crossValidationStorageEntry.getAnnotationEntry().getAnnotation());

        for (String validationTarget : validationTargets) {
            validationTarget = validationTarget.trim();

            //select validation method
            tryToValidate(crossValidationStorageEntry, crossValidationStorage, validationTarget);
        }
    }

    private boolean tryToValidate(CrossValidationStorageEntry crossValidationStorageEntry, CrossValidationStorage crossValidationStorage, String validationTarget) {
        for (ReferencingStrategy referencingStrategy : referencingStrategies) {
            if (referencingStrategy.evalReferenceAndValidate(crossValidationStorageEntry, crossValidationStorage, validationTarget, this)) {
                return true;
            }
        }

        return false;
    }

    //has to be public for custom referencing strategies!!!
    public void handleTargetViolation(CrossValidationStorageEntry crossValidationStorageEntry, CrossValidationStorageEntry entry) {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        //get validation error messages for the target component
        String summary = getErrorMessageSummary(crossValidationStorageEntry.getAnnotationEntry().getAnnotation(), true);
        String details = getErrorMessageDetails(crossValidationStorageEntry.getAnnotationEntry().getAnnotation(), true);

        //validation target isn't bound to a component withing the current page (see validateFoundEntry, tryToValidateLocally and tryToValidateBindingOnly)
        if (entry == null) {
            entry = crossValidationStorageEntry;
        }

        FacesMessage message;
        if (entry.getAnnotationEntry() != null) {
            message = getTargetComponentErrorMessage(entry.getAnnotationEntry().getAnnotation(), summary, details);
        } else {
            //TODO document possible side effects
            //due to a missing target annotation (see: tryToValidateLocally)
            message = getTargetComponentErrorMessage(crossValidationStorageEntry.getAnnotationEntry().getAnnotation(), summary, details);
        }

        if (message.getSummary() != null || message.getDetail() != null) {
            facesContext.addMessage(entry.getComponent().getClientId(facesContext), message);
        }
    }

    //has to be public for custom referencing strategies!!!
    public void processSourceComponentAfterViolation(CrossValidationStorageEntry crossValidationStorageEntry) {

        //get validation error messages for the current component
        String summary = getErrorMessageSummary(crossValidationStorageEntry.getAnnotationEntry().getAnnotation(), false);
        String details = getErrorMessageDetails(crossValidationStorageEntry.getAnnotationEntry().getAnnotation(), false);

        FacesMessage message = getSourceComponentErrorMessage(crossValidationStorageEntry.getAnnotationEntry().getAnnotation(), summary, details);

        if (message.getSummary() != null || message.getDetail() != null) {
            //TODO
            throw new ValidatorException(message);
        } else {
            throw new ValidatorException(new FacesMessage());
        }
    }

    //has to be public for custom referencing strategies!!!
    public FacesMessage getSourceComponentErrorMessage(Annotation annotation, String summary, String details) {
        FacesMessage message = new FacesMessage();

        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        message.setSummary(summary);
        message.setDetail(details);

        return message;
    }

    //has to be public for custom referencing strategies!!!
    public FacesMessage getTargetComponentErrorMessage(Annotation foundAnnotation, String summary, String details) {
        FacesMessage message = new FacesMessage();

        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        message.setSummary(summary);
        message.setDetail(details);

        return message;
    }

    //has to be public for custom referencing strategies!!!
    public ProcessedInformationEntry resolveValidationTargetEntry(Map<String, ProcessedInformationEntry> valueBindingConvertedValueMapping, String targetValueBinding, Object bean) {
        ProcessedInformationEntry processedInformationEntry = valueBindingConvertedValueMapping.get(targetValueBinding);

        //simple case
        if (processedInformationEntry.getFurtherEntries() == null) {
            return processedInformationEntry;
        }

        //process complex component entries (e.g. a table)
        //supported: cross-component but no cross-entity validation (= locale validation)
        if (processedInformationEntry.getBean().equals(bean)) {
            return processedInformationEntry;
        }

        for (ProcessedInformationEntry entry : processedInformationEntry.getFurtherEntries()) {
            if (entry.getBean().equals(bean)) {
                return entry;
            }
        }

        return null;
    }

    protected String getErrorMessageSummary(Annotation annotation, boolean isTargetComponent) {
        return resolveMessage(getValidationErrorMsgKey(annotation, isTargetComponent));
    }

    protected String getErrorMessageDetails(Annotation annotation, boolean isTargetComponent) {
        try {
            String key = getValidationErrorMsgKey(annotation, isTargetComponent);
            return (key != null) ? resolveMessage(key + DETAIL_MESSAGE_KEY_POSTFIX) : null;
        } catch (MissingResourceException e) {
            logger.warn("couldn't find key " + getValidationErrorMsgKey(annotation, isTargetComponent) + DETAIL_MESSAGE_KEY_POSTFIX, e);
        }
        return null;
    }

    protected String getValidationErrorMsgKey(Annotation annotation) {
        return getValidationErrorMsgKey(annotation, false);
    }

    /*
     * recommended methods to override - have to be public for custom referencing strategies!!!
     */

    /*
     * abstract methods
     */

    protected abstract String getValidationErrorMsgKey(Annotation annotation, boolean isTargetComponent);

    public abstract boolean useTargetComponentToDisplayErrorMsg(CrossValidationStorageEntry crossValidationStorageEntry);

    /*
     * implements the specific validation logic
     */

    public abstract boolean isViolation(Object object1, Object object2, Annotation annotation);

    /*
     * returns the referenced validation targets of the annotation
     * e.g. @DateIs(type = DateIsType.before, value = "finalExam")
     * -> method returns an array with one value ("finalExam")
     */
    public abstract String[] getValidationTargets(Annotation annotation);
}