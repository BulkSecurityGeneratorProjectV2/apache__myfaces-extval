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
package org.apache.myfaces.extensions.validator.crossval.annotation;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ValidationParameter;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ViolationSeverity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.text.DateFormat;

/**
 * @since 1.x.1
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
@UsageInformation(UsageCategory.API)
public @interface DateIs
{
    String[] valueOf();

    /*
     * optional section
     */

    DateIsType type() default DateIsType.same;

    String validationErrorMsgKey() default "";

    MessageTarget validationErrorMsgTarget() default MessageTarget.target;

    String notBeforeErrorMsgKey() default "wrong_date_not_before";

    String notAfterErrorMsgKey() default "wrong_date_not_after";

    String notBeforeOrSameErrorMsgKey() default "wrong_date_not_before_or_same";

    String notAfterOrSameErrorMsgKey() default "wrong_date_not_after_or_same";

    String notEqualErrorMsgKey() default "wrong_date_not_equal";

    int errorMessageDateStyle() default DateFormat.MEDIUM;

    String errorMessageDatePattern() default "";

    Class<? extends ValidationParameter>[] parameters() default ViolationSeverity.Error.class;
}
