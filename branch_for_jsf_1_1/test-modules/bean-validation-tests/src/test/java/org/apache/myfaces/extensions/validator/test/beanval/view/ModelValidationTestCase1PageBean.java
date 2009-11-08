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
package org.apache.myfaces.extensions.validator.test.beanval.view;

import org.apache.myfaces.extensions.validator.beanval.annotation.BeanValidation;
import org.apache.myfaces.extensions.validator.beanval.annotation.ModelValidation;
import org.apache.myfaces.extensions.validator.test.beanval.model.ModelValidationTestCase1Bean;

public class ModelValidationTestCase1PageBean
{
    @BeanValidation(modelValidation = @ModelValidation(isActive = true))
    private ModelValidationTestCase1Bean model1 = new ModelValidationTestCase1Bean();

    @BeanValidation(modelValidation = @ModelValidation(isActive = true, displayInline = true))
    private ModelValidationTestCase1Bean model2 = new ModelValidationTestCase1Bean();

    public ModelValidationTestCase1Bean getModel1()
    {
        return model1;
    }

    public void setModel1(ModelValidationTestCase1Bean model1)
    {
        this.model1 = model1;
    }

    public ModelValidationTestCase1Bean getModel2()
    {
        return model2;
    }

    public void setModel2(ModelValidationTestCase1Bean model2)
    {
        this.model2 = model2;
    }
}
