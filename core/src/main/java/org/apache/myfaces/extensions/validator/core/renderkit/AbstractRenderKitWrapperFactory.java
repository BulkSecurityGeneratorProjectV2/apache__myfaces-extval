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
package org.apache.myfaces.extensions.validator.core.renderkit;

import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.core.factory.ClassMappingFactory;
import org.apache.myfaces.extensions.validator.util.JsfUtils;

import javax.faces.render.RenderKit;
import java.util.logging.Logger;

/**
 * Base for all RenderKitWrapperFactories to force a specific behaviour
 *
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.API)
public abstract class AbstractRenderKitWrapperFactory implements ClassMappingFactory<RenderKit, RenderKit>
{
    protected final Logger logger = Logger.getLogger(getClass().getName());

    protected AbstractRenderKitWrapperFactory wrapped;
    private boolean deactivated = false;

    protected AbstractRenderKitWrapperFactory()
    {
        logger.fine(getClass().getName() + " instantiated");
    }

    public void addRenderKitWrapperFactory(AbstractRenderKitWrapperFactory renderKitWrapperFactory)
    {
        logger.finest(renderKitWrapperFactory.getClass().getName() + " added");

        if(this.wrapped != null)
        {
            this.wrapped.addRenderKitWrapperFactory(renderKitWrapperFactory);
            return;
        }

        this.wrapped = renderKitWrapperFactory;
    }

    public void deactivate()
    {
        logger.finest(getClass().getName() + " deactivated");

        this.deactivated = true;
    }

    public boolean isDeactivated()
    {
        return deactivated;
    }

    public final RenderKit create(RenderKit renderKit)
    {
        if(isDeactivated())
        {
            return null;
        }

        RenderKit result = null;

        if(this.wrapped != null)
        {
            result = this.wrapped.create(renderKit);
        }

        if(result == null)
        {
            return createWrapper(renderKit);
        }

        return result;
    }

    protected abstract RenderKit createWrapper(RenderKit renderKit);

    /**
     * simple test for early config in case of mojarra (incl. the combination with trinidad).
     * use a custom extval context impl. (see EXTVAL-58) to optimize this check for the target runtime.
     * this check works for all current implementations since the jsf internals are autom. ready during a request
     * @return true if the jsf impl. is initialized and it's possible to use it as expected
     */
    protected boolean isApplicationInitialized()
    {
        return JsfUtils.isApplicationInitialized();
    }
}
