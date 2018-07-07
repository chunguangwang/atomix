/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.semaphore.impl;

import io.atomix.core.semaphore.AsyncAtomicSemaphore;
import io.atomix.core.semaphore.AtomicSemaphore;
import io.atomix.core.semaphore.AtomicSemaphoreBuilder;
import io.atomix.core.semaphore.AtomicSemaphoreConfig;
import io.atomix.primitive.PrimitiveManagementService;
import io.atomix.primitive.proxy.ProxyClient;

import java.util.concurrent.CompletableFuture;

public class DefaultAtomicSemaphoreBuilder extends AtomicSemaphoreBuilder {
  public DefaultAtomicSemaphoreBuilder(String name, AtomicSemaphoreConfig config, PrimitiveManagementService managementService) {
    super(name, config, managementService);
  }

  @SuppressWarnings("unchecked")
  @Override
  public CompletableFuture<AtomicSemaphore> buildAsync() {
    ProxyClient<AtomicSemaphoreService> proxy = protocol().newProxy(
        name(),
        primitiveType(),
        AtomicSemaphoreService.class,
        new AtomicSemaphoreServiceConfig().setInitialCapacity(config.initialCapacity()),
        managementService.getPartitionService());

    return new AtomicSemaphoreProxy(
        proxy,
        managementService.getPrimitiveRegistry(),
        managementService.getExecutorService())
        .connect()
        .thenApply(AsyncAtomicSemaphore::sync);
  }
}