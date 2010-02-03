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
package org.apache.myfaces.extensions.validator.beanval.validation.message.interpolator;

import org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.AbstractValidationErrorMessageResolver;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

import javax.validation.MessageInterpolator;
import java.util.Locale;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@UsageInformation(UsageCategory.INTERNAL)
public class ExtValMessageInterpolatorAdapter extends DefaultMessageInterpolator
{
    private MessageResolver messageResolver;

    public ExtValMessageInterpolatorAdapter(MessageInterpolator wrapped, MessageResolver messageResolver)
    {
        super(wrapped);
        this.messageResolver = messageResolver;
    }

    @Override
    public String interpolate(String messageOrKey, Context context)
    {
        return interpolate(messageOrKey, context, getCurrentLocale());
    }

    @Override
    public String interpolate(String messageOrKey, Context context, Locale locale)
    {
        if(this.messageResolver != null)
        {
            if(isBeanValidationMessageKeyFormat(messageOrKey))
            {
                String newMessageOrKey = this.messageResolver.getMessage(extractKey(messageOrKey), getCurrentLocale());

                if(isValideMessage(newMessageOrKey))
                {
                    messageOrKey = newMessageOrKey;
                }
            }
            else
            {
                if(this.logger.isTraceEnabled())
                {
                    this.logger.trace("you tried to use an extval message-resolver for" +
                            "jsr303 validation with an invalid key -> using a default interpolator");
                }
            }
        }
        return super.interpolate(messageOrKey, context, getCurrentLocale());
    }

    private boolean isBeanValidationMessageKeyFormat(String messageOrKey)
    {
        return messageOrKey.startsWith("{") && messageOrKey.endsWith("}");
    }

    private String extractKey(String messageOrKey)
    {
        return messageOrKey.substring(1, messageOrKey.length() - 1);
    }

    private boolean isValideMessage(String newMessageOrKey)
    {
        return !(newMessageOrKey.startsWith(AbstractValidationErrorMessageResolver.MISSING_RESOURCE_MARKER) &&
                        newMessageOrKey.endsWith(AbstractValidationErrorMessageResolver.MISSING_RESOURCE_MARKER));
    }
}
