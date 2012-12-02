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
package org.apache.myfaces.extensions.validator.test.core.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import junit.framework.Test;

import org.apache.myfaces.extensions.validator.core.DefaultExtValCoreConfiguration;
import org.apache.myfaces.extensions.validator.core.ExtValCoreConfiguration;
import org.apache.myfaces.extensions.validator.core.storage.PropertyStorage;
import org.apache.myfaces.extensions.validator.test.base.util.ClassLoaderTestSuite;
import org.apache.myfaces.extensions.validator.test.core.config.support.ConstraintSourceAwareBean;
import org.apache.myfaces.extensions.validator.test.core.config.support.CustomTargetProperty;
import org.apache.myfaces.extensions.validator.util.ConstraintSourceUtils;
import org.apache.myfaces.extensions.validator.util.ExtValAnnotationUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;

/**
 * 
 * since v4
 *
 */
public class ExtValCoreConfigurationTargetPropertyAnnotationTestCase extends ExtValCoreConfigurationTestCase
{
    public ExtValCoreConfigurationTargetPropertyAnnotationTestCase(String name)
    {
        super(name);
    }

    @Override
    protected ExtValCoreConfiguration getCustomExtValCoreConfiguration()
    {
        if (needCustomConfig())
        {

            return new DefaultExtValCoreConfiguration()
            {
                @Override
                public Class<? extends Annotation> targetPropertyAnnotation()
                {
                    return CustomTargetProperty.class;
                }
            };
        }
        else
        {
            return null;
        }
    }

    public void testTargetPropertyAnnotationDefault() throws Exception
    {
        Method method = ReflectionUtils.getMethod(ConstraintSourceUtils.class, "getTargetPropertyMetaData",
                PropertyStorage.class, Class.class, String.class);
        assertNotNull(method);
        PropertyStorage storage = ReflectionUtils.getPropertyStorage();
        Annotation target = (Annotation) ReflectionUtils.invokeMethodOfClass(ConstraintSourceUtils.class, method,
                storage, ConstraintSourceAwareBean.class, "property3");

        assertEquals("test1", ExtValAnnotationUtils.extractValueOf(target, Object.class));

        assertNull(ReflectionUtils.invokeMethodOfClass(ConstraintSourceUtils.class, method, storage,
                ConstraintSourceAwareBean.class, "property4"));
    }

    public void testTargetPropertyAnnotationCustomConfig() throws Exception
    {
        Method method = ReflectionUtils.getMethod(ConstraintSourceUtils.class, "getTargetPropertyMetaData",
                PropertyStorage.class, Class.class, String.class);
        assertNotNull(method);
        PropertyStorage storage = ReflectionUtils.getPropertyStorage();
        Annotation target = (Annotation) ReflectionUtils.invokeMethodOfClass(ConstraintSourceUtils.class, method,
                storage, ConstraintSourceAwareBean.class, "property4");

        assertEquals("test2", ExtValAnnotationUtils.extractValueOf(target, Object.class));

        assertNull(ReflectionUtils.invokeMethodOfClass(ConstraintSourceUtils.class, method, storage,
                ConstraintSourceAwareBean.class, "property3"));
    }

    public static Test suite()
    {

        return new ClassLoaderTestSuite(ExtValCoreConfigurationTargetPropertyAnnotationTestCase.class);
    }

}
