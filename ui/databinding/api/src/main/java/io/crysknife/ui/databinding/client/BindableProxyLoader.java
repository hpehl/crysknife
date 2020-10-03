/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package io.crysknife.ui.databinding.client;

import io.crysknife.ui.databinding.client.api.Bindable;

/**
 * This interface is used internally during compile time to produce the required proxies for
 * {@link Bindable} types (see the GWT module descriptor).
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface BindableProxyLoader {

  /**
   * Registers the generated proxies for bindable types.
   */
  public void loadBindableProxies();

}