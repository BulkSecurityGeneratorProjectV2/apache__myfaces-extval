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
package org.apache.myfaces.extensions.validator.core.validation.parameter;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;

import javax.faces.application.FacesMessage;

/**
 * ValidationParameter to indicate the severity of the FacesMessage in case of a validation failure. By default, the
 * severity is Error.
 *
 * @since x.x.3
 */
@UsageInformation(UsageCategory.API)
public interface ViolationSeverity
{
    /**
     * ViolationSeverity Info.
     */
    interface Info extends ValidationParameter
    {
        @ParameterKey
        public Class KEY = ViolationSeverity.class;
                
        @ParameterValue
        public FacesMessage.Severity SEVERITY = FacesMessage.SEVERITY_INFO;

        //@ParameterValue
        //MessageType postfix = MessageType.INFO;
    }

    /**
     * ViolationSeverity Warn.
     */
    interface Warn extends ValidationParameter
    {
        @ParameterKey
        public Class KEY = ViolationSeverity.class;

        @ParameterValue
        FacesMessage.Severity SEVERITY = FacesMessage.SEVERITY_WARN;

        //@ParameterValue
        //MessageType postfix = MessageType.WARN;
    }

    /**
     * ViolationSeverity Error.
     */
    interface Error extends ValidationParameter
    {
        @ParameterKey
        public Class KEY = ViolationSeverity.class;

        @ParameterValue
        FacesMessage.Severity SEVERITY = FacesMessage.SEVERITY_ERROR;

        //@ParameterValue
        //MessageType postfix = MessageType.ERROR;
    }

    /**
     * ViolationSeverity Fatal.
     */
    interface Fatal extends ValidationParameter
    {
        @ParameterKey
        public Class KEY = ViolationSeverity.class;

        @ParameterValue
        FacesMessage.Severity SEVERITY = FacesMessage.SEVERITY_FATAL;

        //@ParameterValue
        //MessageType postfix = MessageType.FATAL;
    }
}
