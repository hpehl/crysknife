/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.treblereel.gwt.crysknife.internal;

import org.treblereel.gwt.crysknife.internal.api.Qualifier;
import org.treblereel.gwt.crysknife.utils.HashUtil;

import javax.lang.model.element.TypeElement;
import java.util.Objects;

public class InjectableHandle {
  final TypeElement type;

  final Qualifier qualifier;

  public InjectableHandle(final TypeElement type, final Qualifier qualifier) {
    this.type = type;
    this.qualifier = qualifier;
  }

  /**
   * @return The class of the injectable represented by this handle.
   */
  public TypeElement getType() {
    return type;
  }

  /**
   * @return The qualifier of the injectable represented by this handle.
   */
  public Qualifier getQualifier() {
    return qualifier;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof InjectableHandle))
      return false;

    final InjectableHandle other = (InjectableHandle) obj;
    return type.equals(other.type) && qualifier.equals(other.qualifier);
  }

  @Override
  public int hashCode() {
    return HashUtil.combineHashCodes(Objects.hashCode(type), Objects.hashCode(qualifier));
  }

  @Override
  public String toString() {
    return String.format("%s %s", getQualifier(), getType().getQualifiedName().toString());
  }
}
