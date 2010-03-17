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
package org.apache.myfaces.extensions.validator.core.storage;

import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import static org.apache.myfaces.extensions.validator.internal.UsageCategory.INTERNAL;

import java.util.Map;
import java.util.HashMap;

/**
 * default storage implementation for groups
 *
 * @author Gerhard Petracek
 * @since x.x.3
 */
@UsageInformation(INTERNAL)
public class DefaultRendererProxyStorage implements RendererProxyStorage
{
    Map<String, Map<String, RendererProxyStorageEntry>> proxyStorage =
        new HashMap<String, Map<String, RendererProxyStorageEntry>>();

    public void setEntry(String rendererKey, String clientId, RendererProxyStorageEntry entry)
    {
        getRendererStorage(rendererKey).put(clientId, entry);
    }

    public boolean containsEntry(String rendererKey, String clientId)
    {
        return getRendererStorage(rendererKey).containsKey(clientId);
    }

    public RendererProxyStorageEntry getEntry(String rendererKey, String clientId)
    {
        return getRendererStorage(rendererKey).get(clientId);
    }

    private Map<String, RendererProxyStorageEntry> getRendererStorage(String rendererKey)
    {
        if(!proxyStorage.containsKey(rendererKey))
        {
            proxyStorage.put(rendererKey, new HashMap<String, RendererProxyStorageEntry>());
        }

        return proxyStorage.get(rendererKey);
    }
}