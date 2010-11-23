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
package org.apache.myfaces.extensions.validator.beanval.util;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.util.JsfUtils;

import java.util.MissingResourceException;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@UsageInformation(UsageCategory.INTERNAL)
class LabeledMessageInternals
{
    //there is no concurrency issue here - it always leads to the same result
    private final String defaultSummaryMessageTemplate = "{1}: {0}";
    private final String defaultDetailMessageTemplate = "{1}: {0}";
    private String summaryMessageTemplate = defaultSummaryMessageTemplate;
    private String detailMessageTemplate = defaultDetailMessageTemplate;
    private static final String JAVAX_FACES_VALIDATOR_BEANVALIDATOR_MESSAGE =
            "javax.faces.validator.BeanValidator.MESSAGE";
    private static final String JAVAX_FACES_VALIDATOR_BEANVALIDATOR_MESSAGE_DETAIL =
            "javax.faces.validator.BeanValidator.MESSAGE_detail";

    String createLabeledMessage(String violationMessage, boolean isDetailMessage)
    {
        if(isDetailMessage)
        {
            return tryToResolveDetailMessage(violationMessage);
        }
        else
        {
            return tryToResolveSummaryMessage(violationMessage);
        }
    }

    private String tryToResolveSummaryMessage(String violationMessage)
    {
        if(summaryMessageTemplate == null)
        {
            return this.defaultSummaryMessageTemplate.replace("{0}", violationMessage);
        }

        this.summaryMessageTemplate = loadStandardMessageTemplate(false);

        if(summaryMessageTemplate == null)
        {
            return createLabeledMessage(violationMessage, false);
        }
        return summaryMessageTemplate.replace("{0}", violationMessage);
    }

    private String tryToResolveDetailMessage(String violationMessage)
    {
        if(detailMessageTemplate == null)
        {
            return this.defaultDetailMessageTemplate.replace("{0}", violationMessage);
        }

        this.detailMessageTemplate = loadStandardMessageTemplate(true);

        if(detailMessageTemplate == null)
        {
            return createLabeledMessage(violationMessage, true);
        }
        return detailMessageTemplate.replace("{0}", violationMessage);
    }

    private String loadStandardMessageTemplate(boolean isDetailMessage)
    {
        try
        {
            if(isDetailMessage)
            {
                return JsfUtils.getMessageFromApplicationMessageBundle(
                        JAVAX_FACES_VALIDATOR_BEANVALIDATOR_MESSAGE_DETAIL);
            }
            else
            {
                return JsfUtils.getMessageFromApplicationMessageBundle(
                        JAVAX_FACES_VALIDATOR_BEANVALIDATOR_MESSAGE);
            }
        }
        catch (MissingResourceException e)
        {
            return null;
        }
    }
}
