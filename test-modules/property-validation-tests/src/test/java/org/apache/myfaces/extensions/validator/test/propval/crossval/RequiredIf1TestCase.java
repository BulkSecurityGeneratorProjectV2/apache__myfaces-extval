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
package org.apache.myfaces.extensions.validator.test.propval.crossval;

import org.apache.myfaces.extensions.validator.test.propval.AbstractPropertyValidationTestCase;
import org.junit.Test;

import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.application.FacesMessage;

public class RequiredIf1TestCase extends AbstractPropertyValidationTestCase
{

    HtmlInputText inputComponent1 = null;
    HtmlInputText inputComponent2 = null;

    UIViewRoot rootComponent = null;

    public RequiredIf1TestCase()
    {
        inputComponent1 = null;
        inputComponent2 = null;
        rootComponent = null;
    }


    @Override
    protected void setUpTestCase()
    {
        super.setUpTestCase();
        RequiredIf1TestBean bean = new RequiredIf1TestBean();
        createValueBinding(null, "value", "#{testBean}");
        facesContext.getExternalContext().getRequestMap().put("testBean", bean);

        rootComponent = new UIViewRoot();
        HtmlForm form = new HtmlForm();
        form.setId("form");
        rootComponent.getChildren().add(form);
        inputComponent1 = new HtmlInputText();
        form.getChildren().add(inputComponent1);
        inputComponent1.setId("input1");
        inputComponent2 = new HtmlInputText();
        form.getChildren().add(inputComponent2);
        inputComponent2.setId("input2");
    }

    @Test
    public void testRequiredIfTargetNotEmptyFailed() throws Exception
    {
        createValueBinding(inputComponent1, "value", "#{testBean.property1}");
        createValueBinding(inputComponent2, "value", "#{testBean.property2}");

        //decode
        inputComponent1.setSubmittedValue("1d3");
        inputComponent2.setSubmittedValue("");

        //validate
        inputComponent1.validate(facesContext);
        inputComponent2.validate(facesContext);

        processCrossValidation();
        checkMessageCount(1);

        assertNavigationBlocked(true);
        checkMessageSeverities(FacesMessage.SEVERITY_ERROR);

        //no update model needed
    }

    @Test
    public void testRequiredIfTargetNotEmptyCorrect1() throws Exception
    {
        createValueBinding(inputComponent1, "value", "#{testBean.property1}");
        createValueBinding(inputComponent2, "value", "#{testBean.property2}");

        //decode
        inputComponent1.setSubmittedValue("1d3");
        inputComponent2.setSubmittedValue("1d3");

        //validate
        inputComponent1.validate(facesContext);
        inputComponent2.validate(facesContext);

        processCrossValidation();
        checkMessageCount(0);

        assertNavigationBlocked(false);

        //no update model needed
    }

    @Test
    public void testRequiredIfTargetNotEmptyCorrect2() throws Exception
    {
        createValueBinding(inputComponent1, "value", "#{testBean.property1}");
        createValueBinding(inputComponent2, "value", "#{testBean.property2}");

        //decode
        inputComponent1.setSubmittedValue("");
        inputComponent2.setSubmittedValue("");

        //validate
        inputComponent1.validate(facesContext);
        inputComponent2.validate(facesContext);

        processCrossValidation();
        checkMessageCount(0);

        assertNavigationBlocked(false);

        //no update model needed
    }

    @Test
    public void testRequiredIfTargetNotEmptyCorrect3() throws Exception
    {
        createValueBinding(inputComponent1, "value", "#{testBean.property1}");
        createValueBinding(inputComponent2, "value", "#{testBean.property2}");

        //decode
        inputComponent1.setSubmittedValue("");
        inputComponent2.setSubmittedValue("1d3");

        //validate
        inputComponent1.validate(facesContext);
        inputComponent2.validate(facesContext);

        processCrossValidation();
        checkMessageCount(0);

        assertNavigationBlocked(false);

        //no update model needed
    }
}