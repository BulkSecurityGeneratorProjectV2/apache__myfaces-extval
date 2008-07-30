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
package org.apache.myfaces.extensions.validator.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.convert.Converter;

/**
 * due to a restriction at the state saving process
 * a proxy gets a super-class which might impl. StateHolder
 * -> saveState: a StateHolder impl. gets handled before Serializable -> callback doesn't get saved
 * -> restoreState: restoreState of the super-class gets called - no callback -> no interceptor gets executed
 * if a framework (such as trinidad) provides caching it's no problem
 * -> use the web.xml context-param to deactivate the mechanism
 *
 * @author Gerhard Petracek
 */
public class ProxyMappingPhaseListener implements PhaseListener {
    private boolean isInitialized = false;

    protected final Log logger = LogFactory.getLog(getClass());

    public void afterPhase(PhaseEvent event) {
        if (!isInitialized) {
            //don't use DEACTIVATE_PROXY_MAPPING here to allow a different concept
            String initParam = WebXmlParameter.DEACTIVATE_RESTORE_PROXY_PHASE_LISTENER;

            if(initParam != null && initParam.equalsIgnoreCase("true")) {
                ExtValUtils.deregisterPhaseListener(this);
            }
            isInitialized = true;
        }

        if(!event.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
            return;
        }

        Integer processedConverterCount = ExtValUtils.getProcessedConverterCount();
        //don't change the comparison with 0 - in order to reduce the overhead.
        //if everything works correctly it's not necessary to inspact the full tree
        //it's just due to a ri bug - normally it's performed during ExtValConverter#intercept#getAsString
        if(ExtValUtils.useProxyMapping() && (processedConverterCount != null && !processedConverterCount.equals(0))){
            storeComponentConverterMappingForProxies(event.getFacesContext(), event.getFacesContext().getViewRoot());
        }
    }

    public void beforePhase(PhaseEvent event) {
        if(!event.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
            return;
        }

        ExtValUtils.restoreProxies();
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    /**
     * there is a ri bug (at least with jsp's) -> sometimes getAsString of converters aren't called
     * -> there is no mapping -> if it's the case and there are unhandled editable value hoder components within the page
     * -> search all these components and add the equivalent converter to the mapping
     *
     * @param facesContext reference to the current faces context
     * @param uiComponent reference to the current component
     */
    private void storeComponentConverterMappingForProxies(FacesContext facesContext, UIComponent uiComponent) {
        //TODO use the following after the impl. of a better multi-window-mode solution
        //if(!uiComponent.isRendered()) {
        //    return;
        //}
        
        for(UIComponent child : uiComponent.getChildren()) {
            if(child instanceof ValueHolder) {
                Converter converter = ((ValueHolder)child).getConverter();
                if(converter != null && converter.getClass().getName().contains("$$")) {
                    ExtValUtils.getOrInitProxyMapping().put(child.getClientId(facesContext), child);
                }
            }
            storeComponentConverterMappingForProxies(facesContext, child);
        }
    }
}