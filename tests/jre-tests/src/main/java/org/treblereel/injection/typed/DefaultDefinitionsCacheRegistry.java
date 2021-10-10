/*
 * Copyright © 2021 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.treblereel.injection.typed;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/9/21
 */
@Dependent
@Typed(DefaultDefinitionsCacheRegistry.class)
public class DefaultDefinitionsCacheRegistry implements DefinitionsCacheRegistry {
  @Override
  public Object getDefinitionById(String id) {
    return null;
  }

  @Override
  public void clear() {

  }

  @Override
  public Set<String> getLabels(String id) {
    return null;
  }

  @Override
  public void register(Object item) {

  }

  @Override
  public boolean remove(Object item) {
    return false;
  }

  @Override
  public boolean contains(Object item) {
    return false;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  public DefaultDefinitionsCacheRegistry useStorage(
      final Supplier<Map<String, ?>> storageSupplier) {
    return this;
  }
}
