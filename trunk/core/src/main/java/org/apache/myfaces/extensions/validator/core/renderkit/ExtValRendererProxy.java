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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import javax.faces.component.UIComponent;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * to avoid multiple calls of renderer methods within renderer interceptors (e.g. for encode, decode,...)
 *
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.INTERNAL)
public class ExtValRendererProxy extends Renderer
{
    public static final String KEY = ExtValRendererProxy.class.getName() + ":KEY";
    protected final Log logger = LogFactory.getLog(getClass());
    
    protected Renderer wrapped;

    public ExtValRendererProxy(Renderer renderer)
    {
        this.wrapped = renderer;

        if(logger.isTraceEnabled())
        {
            logger.trace("proxy created for " + renderer.getClass().getName());
        }
    }

    @Override
    public void decode(FacesContext facesContext, UIComponent uiComponent)
    {
        RendererProxyEntry entry = getOrInitEntry(facesContext, uiComponent);

        if (!entry.isDecodeCalled())
        {
            entry.setDecodeCalled(true);

            try
            {
                this.wrapped.decode(facesContext, uiComponent);
            }
            catch (RuntimeException r)
            {
                resetComponentProxyMapping();
                throw r;
            }
        }
        else
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("double call of method 'decode' filtered");
            }
        }
    }

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
        throws IOException
    {
        RendererProxyEntry entry = getOrInitEntry(facesContext, uiComponent);

        if (!entry.isEncodeBeginCalled())
        {
            entry.setEncodeBeginCalled(true);
            try
            {
                this.wrapped.encodeBegin(facesContext, uiComponent);
            }
            catch (IOException e)
            {
                resetComponentProxyMapping();
                throw e;
            }
            catch (RuntimeException r)
            {
                resetComponentProxyMapping();
                throw r;
            }
        }
        else
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("double call of method 'encodeBegin' filtered");
            }
        }
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent uiComponent)
        throws IOException
    {
        RendererProxyEntry entry = getOrInitEntry(facesContext, uiComponent);

        if (!entry.isEncodeChildrenCalled())
        {
            entry.setEncodeChildrenCalled(true);

            try
            {
                this.wrapped.encodeChildren(facesContext, uiComponent);
            }
            catch (IOException e)
            {
                resetComponentProxyMapping();
                throw e;
            }
            catch (RuntimeException r)
            {
                resetComponentProxyMapping();
                throw r;
            }
        }
        else
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("double call of method 'encodeChildren' filtered");
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
        throws IOException
    {
        RendererProxyEntry entry = getOrInitEntry(facesContext, uiComponent);

        if (!entry.isEncodeEndCalled())
        {
            entry.setEncodeEndCalled(true);

            try
            {
                this.wrapped.encodeEnd(facesContext, uiComponent);
            }
            catch (IOException e)
            {
                resetComponentProxyMapping();
                throw e;
            }
            catch (RuntimeException r)
            {
                resetComponentProxyMapping();
                throw r;
            }
        }
        else
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("double call of method 'encodeEnd' filtered");
            }
        }
    }

    @Override
    public String convertClientId(FacesContext facesContext, String s)
    {
        try
        {
            return wrapped.convertClientId(facesContext, s);
        }
        catch (RuntimeException r)
        {
            resetComponentProxyMapping();
            throw r;
        }
    }

    @Override
    public boolean getRendersChildren()
    {
        try
        {
            return wrapped.getRendersChildren();
        }
        catch (RuntimeException t)
        {
            resetComponentProxyMapping();
            throw t;
        }
    }

    @Override
    public Object getConvertedValue(FacesContext facesContext, UIComponent uiComponent, Object o)
        throws ConverterException
    {
        RendererProxyEntry entry = getOrInitEntry(facesContext, uiComponent);

        if (entry.getConvertedValue() == null)
        {
            try
            {
                entry.setConvertedValue(wrapped.getConvertedValue(facesContext, uiComponent, o));
            }
            catch (RuntimeException r)
            {
                resetComponentProxyMapping();
                throw r;
            }
        }
        else
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("double call of method 'getConvertedValue' filtered");
            }
        }
        return entry.getConvertedValue();
    }

    private RendererProxyEntry getOrInitEntry(FacesContext facesContext, UIComponent uiComponent)
    {
        String key = uiComponent.getClientId(facesContext);

        key += getOptionalKey(facesContext, uiComponent);

        if (!getOrInitComponentProxyMapping().containsKey(key))
        {
            getOrInitComponentProxyMapping().put(key, new RendererProxyEntry());
        }
        return getOrInitComponentProxyMapping().get(key);
    }

    protected String getOptionalKey(FacesContext facesContext, UIComponent uiComponent)
    {
        return "";
    }

    private static final String PROXY_STORAGE_NAME = ExtValRendererProxy.class.getName() + ":STORAGE";

    private Map<String, RendererProxyEntry> getOrInitComponentProxyMapping()
    {
        Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

        if(!requestMap.containsKey(PROXY_STORAGE_NAME))
        {
            requestMap.put(PROXY_STORAGE_NAME, new HashMap<String, Map<String, RendererProxyEntry>>());
        }

        Map<String, Map<String, RendererProxyEntry>> proxyStorage =
            ((Map<String, Map<String, RendererProxyEntry>>)requestMap.get(PROXY_STORAGE_NAME));

        String key = this.wrapped.getClass().getName();

        if(!proxyStorage.containsKey(key))
        {
            proxyStorage.put(key, new HashMap<String, RendererProxyEntry>());
        }

        return proxyStorage.get(key);
    }

    private void resetComponentProxyMapping()
    {
        //reset component proxy mapping
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove(PROXY_STORAGE_NAME);
    }
}
