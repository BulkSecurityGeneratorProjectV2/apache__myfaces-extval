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

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

import javax.validation.MessageInterpolator;
import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @since x.x.3
 */
@UsageInformation(UsageCategory.INTERNAL)
public class DefaultMessageInterpolator implements MessageInterpolator
{
    protected final Logger logger = Logger.getLogger(getClass().getName());

    private MessageInterpolator wrapped;

    public DefaultMessageInterpolator(MessageInterpolator wrapped)
    {
        this.wrapped = wrapped;
    }

    public String interpolate(String messageOrKey, Context context)
    {
        return interpolate(messageOrKey, context, null);
    }

    public String interpolate(String messageOrKey, Context context, Locale locale)
    {
        return this.wrapped.interpolate(messageOrKey, context, getCurrentLocale());
    }

    protected Locale getCurrentLocale()
    {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }
}