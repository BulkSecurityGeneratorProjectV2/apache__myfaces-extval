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
package org.apache.myfaces.extensions.validator.test.beanval;

import org.apache.myfaces.extensions.validator.test.beanval.model.TestCase1Bean;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.faces.application.FacesMessage;

public class LabelReplacementTestCase extends BaseBeanValPropertyValidationTestCase<TestCase1Bean>
{
    public LabelReplacementTestCase(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(LabelReplacementTestCase.class);
    }

    protected TestCase1Bean getBeanToTest()
    {
        return new TestCase1Bean();
    }

    public void testLabelReplacement()
    {
        String labelText = "property1";
        createValueBindingForComponent(this.inputComponent1, "#{testBean.property1}");
        this.inputComponent1.setLabel(labelText);
        setValueToValidate(this.inputComponent1, "");

        validateComponents();

        checkMessageCount(1);

        FacesMessage facesMessage = ((FacesMessage)facesContext.getMessages().next());
        assertTrue(facesMessage.getSummary().startsWith(labelText));
        assertTrue(facesMessage.getDetail().startsWith(labelText));
    }
}