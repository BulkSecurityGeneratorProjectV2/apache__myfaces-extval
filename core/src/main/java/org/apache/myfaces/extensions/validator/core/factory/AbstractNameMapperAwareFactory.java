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
package org.apache.myfaces.extensions.validator.core.factory;

import org.apache.myfaces.extensions.validator.core.Nested;
import org.apache.myfaces.extensions.validator.core.mapper.SubMapperAwareNameMapper;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.core.mapper.NameMapper;
import org.apache.myfaces.extensions.validator.core.InvocationOrderComparator;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Generic implementation of a
 * {@link org.apache.myfaces.extensions.validator.core.factory.NameMapperAwareFactory}.
 * A concrete implementation has to provide a list of
 * {@link org.apache.myfaces.extensions.validator.core.mapper.NameMapper name-mappers} which is used to de-/register new
 * {@link org.apache.myfaces.extensions.validator.core.mapper.NameMapper name-mappers} (via
 * {@link #register(org.apache.myfaces.extensions.validator.core.mapper.NameMapper)},
 * {@link #deregister(Class)} and {@link #deny(Class)}).
 *
 * @since 1.x.2
 */
@UsageInformation(UsageCategory.API)
public abstract class AbstractNameMapperAwareFactory<T> implements NameMapperAwareFactory<NameMapper<T>>
{
    private List<Class> deniedNameMapperList = new CopyOnWriteArrayList<Class>();

    public synchronized void register(NameMapper<T> nameMapper)
    {
        if(!deniedNameMapperList.contains(nameMapper.getClass()))
        {
            getNameMapperList().add(nameMapper);
            List<NameMapper<T>> nameMapperList = getNameMapperList();

            if(nameMapperList instanceof CopyOnWriteArrayList)
            {
                List<NameMapper<T>> sortableList = new ArrayList<NameMapper<T>>(nameMapperList);
                Collections.sort(sortableList, getComparator());
                nameMapperList.clear();
                nameMapperList.addAll(sortableList);
            }
            else
            {
                Collections.sort(nameMapperList, getComparator());
            }
        }
    }

    protected Comparator<NameMapper<T>> getComparator()
    {
        return new InvocationOrderComparator<NameMapper<T>>();
    }

    public synchronized void deregister(Class<? extends NameMapper> classToDeregister)
    {
        boolean subNameMapper = false;

        if (classToDeregister != null && classToDeregister.isAnnotationPresent(Nested.class))
        {
            subNameMapper = true;
        }

        List<NameMapper<T>> nameMapperList = getNameMapperList();
        List<NameMapper<T>> changeableList;
        if (!subNameMapper && (getNameMapperList() instanceof CopyOnWriteArrayList))
        {
            // If we have a CopyOnWriteArrayList, we can't remove it so copy it to a temporary list
            changeableList = new ArrayList<NameMapper<T>>(nameMapperList);
        }
        else
        {
            changeableList = nameMapperList;
        }
        Iterator<NameMapper<T>> nameMapperIterator = changeableList.iterator();
        while (nameMapperIterator.hasNext())
        {
            if (subNameMapper)
            {
                NameMapper<T> nameMapper = nameMapperIterator.next();
                if (nameMapper instanceof SubMapperAwareNameMapper)
                {
                    ((SubMapperAwareNameMapper) nameMapper).removeNameMapper(classToDeregister);
                }
            }
            else
            {
                if (nameMapperIterator.next().getClass().getName().equals(classToDeregister.getName()))
                {
                    nameMapperIterator.remove();
                    //don't break - e.g. to deregister all wrappers...
                    //break;
                }
            }
            if (!subNameMapper && (getNameMapperList() instanceof CopyOnWriteArrayList))
            {
                // Set the correct content back in the CopyOnWriteArrayList
                getNameMapperList().clear();
                getNameMapperList().addAll(changeableList);
            }

        }
    }

    public void deny(Class<? extends NameMapper> classToDeny)
    {
        deregister(classToDeny);

        synchronized (getClass())
        {
            deniedNameMapperList.add(classToDeny);
        }
    }

    protected abstract List<NameMapper<T>> getNameMapperList();
}
