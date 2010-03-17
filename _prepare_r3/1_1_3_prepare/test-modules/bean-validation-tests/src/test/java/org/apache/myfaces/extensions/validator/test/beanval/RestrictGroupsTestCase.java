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

import org.apache.myfaces.extensions.validator.test.beanval.view.RestrictGroupValidationTestCase1PageBean;
import org.apache.myfaces.extensions.validator.test.beanval.model.SimulatedUserInformation;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class RestrictGroupsTestCase extends BaseBeanValPropertyValidationTestCase<RestrictGroupValidationTestCase1PageBean>
{
    public RestrictGroupsTestCase(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(RestrictGroupsTestCase.class);
    }

    protected RestrictGroupValidationTestCase1PageBean getBeanToTest()
    {
        return new RestrictGroupValidationTestCase1PageBean();
    }

    public void testRestrictedGroup()
    {
        createValueBindingForComponent(this.inputComponent1, "#{testBean.model1.property1}");
        setValueToValidate(this.inputComponent1, "");

        validateComponents();

        assertComponentValid(this.inputComponent1);
        assertNavigationBlocked(false);

        checkMessageCount(0);
    }

    public void testRestrictedGroupWithAdminRole()
    {
        createRequestScopedBean("currentUser", new SimulatedUserInformation("admin"));

        createValueBindingForComponent(this.inputComponent1, "#{testBean.model2.property1}");
        createValueBindingForComponent(this.inputComponent2, "#{testBean.model2.property2}");
        setValueToValidate(this.inputComponent1, "");
        setValueToValidate(this.inputComponent2, "");

        validateComponents();

        assertComponentValid(this.inputComponent1);
        assertComponentValid(this.inputComponent2);

        assertNavigationBlocked(false);
        checkMessageCount(0);
    }

    public void testRestrictedGroupWithUserRole()
    {
        createRequestScopedBean("currentUser", new SimulatedUserInformation("user"));

        createValueBindingForComponent(this.inputComponent1, "#{testBean.model2.property1}");
        createValueBindingForComponent(this.inputComponent2, "#{testBean.model2.property2}");
        setValueToValidate(this.inputComponent1, "");
        setValueToValidate(this.inputComponent2, "");

        validateComponents();

        assertComponentInvalid(this.inputComponent1);
        assertComponentValid(this.inputComponent2);

        assertNavigationBlocked(true);
        checkMessageCount(1);
        checkMessageSeverities(FacesMessage.SEVERITY_ERROR);
    }

    public void testRestrictedGroupWithCorrectViewId()
    {
        FacesContext.getCurrentInstance().getViewRoot().setViewId("/pages/groupValidationAwarePage.xhtml");

        createValueBindingForComponent(this.inputComponent1, "#{testBean.model3.property1}");
        createValueBindingForComponent(this.inputComponent2, "#{testBean.model3.property2}");
        setValueToValidate(this.inputComponent1, "");
        setValueToValidate(this.inputComponent2, "");

        validateComponents();

        assertComponentValid(this.inputComponent1);
        assertComponentValid(this.inputComponent2);

        assertNavigationBlocked(false);
        checkMessageCount(0);
    }

    public void testRestrictedGroupWithWrongViewId()
    {
        createValueBindingForComponent(this.inputComponent1, "#{testBean.model3.property1}");
        createValueBindingForComponent(this.inputComponent2, "#{testBean.model3.property2}");
        setValueToValidate(this.inputComponent1, "");
        setValueToValidate(this.inputComponent2, "");

        validateComponents();

        assertComponentInvalid(this.inputComponent1);
        assertComponentValid(this.inputComponent2);

        assertNavigationBlocked(true);
        checkMessageCount(1);
        checkMessageSeverities(FacesMessage.SEVERITY_ERROR);
    }

    public void testUsedAndRestrictedGroup()
    {
        createValueBindingForComponent(this.inputComponent1, "#{testBean.model4.property1}");
        createValueBindingForComponent(this.inputComponent2, "#{testBean.model4.property2}");
        setValueToValidate(this.inputComponent1, "");
        setValueToValidate(this.inputComponent2, "");

        validateComponents();

        assertComponentValid(this.inputComponent1);
        assertComponentInvalid(this.inputComponent2);

        assertNavigationBlocked(true);
        checkMessageCount(1);
        checkMessageSeverities(FacesMessage.SEVERITY_ERROR);
    }
}
