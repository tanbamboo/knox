/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.knox.gateway.topology.discovery;

import org.apache.knox.gateway.services.security.AliasService;
import org.apache.knox.gateway.services.security.impl.DefaultAliasService;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;


public class ServiceDiscoveryFactoryTest {

    @Test
    public void testGetDummyImpl() throws Exception {
        String TYPE = "DUMMY";
        ServiceDiscovery sd = ServiceDiscoveryFactory.get(TYPE);
        assertNotNull("Expected to get a ServiceDiscovery object.", sd);
        assertEquals("Unexpected ServiceDiscovery type.", TYPE, sd.getType());
    }


    @Test
    public void testGetDummyImplWithMismatchedCase() throws Exception {
        String TYPE = "dUmmY";
        ServiceDiscovery sd = ServiceDiscoveryFactory.get(TYPE);
        assertNotNull("Expected to get a ServiceDiscovery object.", sd);
        assertEquals("Unexpected ServiceDiscovery type.", TYPE.toUpperCase(), sd.getType());
    }


    @Test
    public void testGetInvalidImpl() throws Exception {
        String TYPE = "InValID";
        ServiceDiscovery sd = ServiceDiscoveryFactory.get(TYPE);
        assertNull("Unexpected ServiceDiscovery object.", sd);
    }


    @Test
    public void testGetImplWithMismatchedType() throws Exception {
        String TYPE = "DeclaredType";
        ServiceDiscovery sd = ServiceDiscoveryFactory.get(TYPE);
        assertNull("Unexpected ServiceDiscovery object.", sd);
    }


    @Test
    public void testGetPropertiesFileImplWithAliasServiceInjection() throws Exception {
        String TYPE = "PROPERTIES_FILE";
        ServiceDiscovery sd = ServiceDiscoveryFactory.get(TYPE, new DefaultAliasService());
        assertNotNull("Expected to get a ServiceDiscovery object.", sd);
        assertEquals("Unexpected ServiceDiscovery type.", TYPE, sd.getType());

        // Verify that the AliasService was injected as expected
        Field aliasServiceField = sd.getClass().getDeclaredField("aliasService");
        aliasServiceField.setAccessible(true);
        Object fieldValue = aliasServiceField.get(sd);
        assertNotNull(fieldValue);
        assertTrue(AliasService.class.isAssignableFrom(fieldValue.getClass()));
    }


}
