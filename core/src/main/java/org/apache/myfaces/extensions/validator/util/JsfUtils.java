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
package org.apache.myfaces.extensions.validator.util;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.core.storage.FacesInformationStorage;

import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import java.util.Iterator;
import java.util.ResourceBundle;


/**
 * @author Gerhard Petracek
 * @since 1.x.1
 */
@UsageInformation(UsageCategory.INTERNAL)
public class JsfUtils
{
    public static void deregisterPhaseListener(PhaseListener phaseListener)
    {
        LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);

        String currentId;
        Lifecycle currentLifecycle;
        Iterator lifecycleIds = lifecycleFactory.getLifecycleIds();
        while (lifecycleIds.hasNext())
        {
            currentId = (String) lifecycleIds.next();
            currentLifecycle = lifecycleFactory.getLifecycle(currentId);
            currentLifecycle.removePhaseListener(phaseListener);
        }
    }

    public static void registerPhaseListener(PhaseListener phaseListener)
    {
        LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);

        String currentId;
        Lifecycle currentLifecycle;
        Iterator lifecycleIds = lifecycleFactory.getLifecycleIds();
        while (lifecycleIds.hasNext())
        {
            currentId = (String) lifecycleIds.next();
            currentLifecycle = lifecycleFactory.getLifecycle(currentId);
            currentLifecycle.addPhaseListener(phaseListener);
        }
    }

    public static ResourceBundle getDefaultFacesMessageBundle()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        return ResourceBundle.getBundle(FacesMessage.FACES_MESSAGES, facesContext.getViewRoot().getLocale());
    }

    public static ResourceBundle getCustomFacesMessageBundle()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String bundleName = facesContext.getApplication().getMessageBundle();

        if(bundleName == null)
        {
            return null;
        }

        return ResourceBundle.getBundle(bundleName, facesContext.getViewRoot().getLocale());
    }

    public static String getMessageFromApplicationMessageBundle(String messageKey)
    {
        ResourceBundle customResourceBundle = getCustomFacesMessageBundle();

        if(customResourceBundle != null && customResourceBundle.containsKey(messageKey))
        {
            return customResourceBundle.getString(messageKey);
        }

        return getDefaultFacesMessageBundle().getString(messageKey);
    }

    public static boolean isRenderResponsePhase()
    {
        return PhaseId.RENDER_RESPONSE.equals(getFacesInformationStorage().getCurrentPhaseId());
    }

    public static PhaseId getCurrentPhaseId()
    {
        return getFacesInformationStorage().getCurrentPhaseId();
    }

    private static FacesInformationStorage getFacesInformationStorage()
    {
        return ExtValUtils.getStorage(FacesInformationStorage.class, FacesInformationStorage.class.getName());
    }
}
