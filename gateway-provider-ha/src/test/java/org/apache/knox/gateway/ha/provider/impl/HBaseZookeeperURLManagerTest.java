/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.knox.gateway.ha.provider.impl;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingCluster;
import org.apache.knox.gateway.ha.provider.HaServiceConfig;
import org.apache.knox.gateway.ha.provider.URLManager;
import org.apache.knox.gateway.ha.provider.URLManagerLoader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple unit tests for HBaseZookeeperURLManager.
 * 
 * @see HBaseZookeeperURLManager
 */
public class HBaseZookeeperURLManagerTest {
	
  private TestingCluster cluster;

  @Before
  public void setup() throws Exception {
    cluster = new TestingCluster(3);
    cluster.start();

    CuratorFramework zooKeeperClient =
        CuratorFrameworkFactory.builder().connectString(cluster.getConnectString())
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    zooKeeperClient.start();
    zooKeeperClient.create().forPath("/hbase-unsecure");
    zooKeeperClient.create().forPath("/hbase-unsecure/rs");
    zooKeeperClient.close();
  }

  @After
  public void teardown() throws IOException {
    cluster.stop();
  }

  @Test
  public void testHBaseZookeeperURLManagerLoading() {
    HaServiceConfig config = new DefaultHaServiceConfig("WEBHBASE");
    config.setEnabled(true);
    config.setZookeeperEnsemble(cluster.getConnectString());
    URLManager manager = URLManagerLoader.loadURLManager(config);
    Assert.assertNotNull(manager);
    Assert.assertTrue(manager instanceof HBaseZookeeperURLManager);
  }
}
