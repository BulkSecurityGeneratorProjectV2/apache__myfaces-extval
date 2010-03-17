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
package org.apache.myfaces.extensions.validator.test.propval.baseval.parameter;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ValidationParameterExtractor;
import org.apache.myfaces.extensions.validator.core.validation.parameter.DefaultValidationParameterExtractor;
import org.apache.myfaces.extensions.validator.core.validation.parameter.DisableClientSideValidation;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ViolationSeverity;
import org.apache.myfaces.extensions.validator.core.interceptor.PropertyValidationInterceptor;
import org.apache.myfaces.extensions.validator.baseval.annotation.Required;

import javax.faces.application.FacesMessage;

public class ParameterTestCase extends TestCase
{
    public static Test suite()
    {
        return new TestSuite(ParameterTestCase.class);
    }

    public void testParameterStyleOne() throws Exception
    {
        ValidationParameterExtractor extractor = new DefaultValidationParameterExtractor();

        TestPerson person = new TestPerson();
        Required required = person.getClass().getDeclaredField("firstName").getAnnotation(Required.class);

        assertNotNull(extractor.extract(required).containsKey(ViolationSeverity.class));
        assertNotNull(extractor.extract(required, ViolationSeverity.class).iterator().next());
        assertEquals(FacesMessage.SEVERITY_WARN, extractor.extract(required, ViolationSeverity.class).iterator().next());
    }

    public void testParameterStyleTwo() throws Exception
    {
        ValidationParameterExtractor extractor = new DefaultValidationParameterExtractor();

        TestPerson person = new TestPerson();
        Required required = person.getClass().getDeclaredField("lastName").getAnnotation(Required.class);

        assertNotNull(extractor.extract(required).containsKey("client_side_validation_support"));
        assertNotNull(extractor.extract(required, "client_side_validation_support").iterator().next());
        assertEquals(false, extractor.extract(required, "client_side_validation_support").iterator().next());
    }

    public void testParameterStyleThree() throws Exception
    {
        ValidationParameterExtractor extractor = new DefaultValidationParameterExtractor();

        TestPerson person = new TestPerson();
        Required required = person.getClass().getDeclaredField("lastName").getAnnotation(Required.class);

        assertNotNull(extractor.extract(required).containsKey(TestPriority.class));
        assertNotNull(extractor.extract(required, TestPriority.class).iterator().next());
        assertEquals(new Integer(1), extractor.extract(required, TestPriority.class, Integer.class).iterator().next());
        assertEquals(2, extractor.extract(required, TestPriority.class, String.class).size());
        assertEquals("do it asap", extractor.extract(required, TestPriority.class, String.class, TestPriority.ShortDescription.class));
        assertEquals("do it immediately", extractor.extract(required, TestPriority.class, String.class, TestPriority.LongDescription.class));
    }

    public void testParameterStyleFour() throws Exception
    {
        ValidationParameterExtractor extractor = new DefaultValidationParameterExtractor();

        TestPerson person = new TestPerson();
        Required required = person.getClass().getDeclaredField("lastName").getAnnotation(Required.class);

        assertNotNull(extractor.extract(required).containsKey(PropertyValidationInterceptor.class));
        assertNotNull(extractor.extract(required, PropertyValidationInterceptor.class).iterator().next());
        assertEquals(1, extractor.extract(required, PropertyValidationInterceptor.class).size());
        assertEquals(TestValidationInterceptor.class.getName(), extractor.extract(required, PropertyValidationInterceptor.class, PropertyValidationInterceptor.class).iterator().next().getClass().getName());
    }

    public void testParameterStyleFive() throws Exception
    {
        ValidationParameterExtractor extractor = new DefaultValidationParameterExtractor();

        TestPerson person = new TestPerson();
        Required required = person.getClass().getDeclaredField("lastName").getAnnotation(Required.class);

        assertNotNull(extractor.extract(required).containsKey(DisableClientSideValidation.class));
        assertNotNull(extractor.extract(required, DisableClientSideValidation.class).iterator().next());
        assertEquals(1, extractor.extract(required, DisableClientSideValidation.class).size());
        assertEquals(1, extractor.extract(required, DisableClientSideValidation.class, Class.class).size());
        assertEquals(DisableClientSideValidation.class.getName(), extractor.extract(required, DisableClientSideValidation.class, Class.class).iterator().next().getName());
    }

    /*
     * TODO these tests work in an ide but not via commandline - it's a Surefire issue
     */
    /*
    public void testParameterStyleSix() throws Exception
    {
        ValidationParameterExtractor extractor = new DefaultValidationParameterExtractor();

        TestPerson person = new TestPerson();
        Required required = person.getClass().getDeclaredField("lastName").getAnnotation(Required.class);

        assertNotNull(extractor.extract(required).containsKey(TestValidatorProvider.class));
        assertNotNull(extractor.extract(required, TestValidatorProvider.class).iterator().next());
        assertEquals(2, extractor.extract(required, TestValidatorProvider.class, Class.class).size());
    }

    public void testParameterStyleSeven() throws Exception
    {
        ValidationParameterExtractor extractor = new DefaultValidationParameterExtractor();

        TestPerson person = new TestPerson();
        Required required = person.getClass().getDeclaredField("lastName").getAnnotation(Required.class);

        assertNotNull(extractor.extract(required).containsKey(TestValidatorProvider.class));
        for (Class currentClass : extractor.extract(required, TestValidatorProvider.class, Class.class))
        {
            assertTrue(TestValidatorProvider.class.isAssignableFrom(currentClass));
        }
    }
    */
}