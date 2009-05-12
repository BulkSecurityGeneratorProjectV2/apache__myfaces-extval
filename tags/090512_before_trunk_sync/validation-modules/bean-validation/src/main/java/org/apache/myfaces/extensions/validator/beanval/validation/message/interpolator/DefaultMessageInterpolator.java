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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.MessageInterpolator;
import javax.validation.ConstraintDescriptor;
import javax.faces.context.FacesContext;
import java.util.Locale;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
public class DefaultMessageInterpolator implements MessageInterpolator
{
    protected final Log logger = LogFactory.getLog(getClass());
    
    private MessageInterpolator wrapped;

    public DefaultMessageInterpolator(MessageInterpolator wrapped)
    {
        this.wrapped = wrapped;
    }

    public String interpolate(String messageOrKey, ConstraintDescriptor constraintDescriptor, Object o)
    {
        return interpolate(messageOrKey, constraintDescriptor, o, null);
    }

    public String interpolate(String messageOrKey, ConstraintDescriptor constraintDescriptor, Object o, Locale locale)
    {
        return this.wrapped.interpolate(messageOrKey, constraintDescriptor, o, getCurrentLocale());
    }

    protected Locale getCurrentLocale()
    {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }
}