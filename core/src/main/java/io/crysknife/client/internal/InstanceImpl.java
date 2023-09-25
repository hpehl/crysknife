/*
 * Copyright © 2020 Treblereel
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
package io.crysknife.client.internal;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import jakarta.annotation.Nonnull;
import jakarta.enterprise.inject.Instance;

import io.crysknife.client.BeanManager;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.SyncBeanDef;

/**
 * TODO this class must be refactored
 *
 * @author Dmitrii Tikhomirov Created by treblereel 3/29/19
 */
public class InstanceImpl<T> implements Instance<T>, InstanceFactory<T> {

  private final BeanManager beanManager;

  private final Class<T> type;

  private final Annotation[] qualifiers;

  public InstanceImpl(BeanManager beanManager, Class<T> type) {
    this(beanManager, type, new Annotation[] {});
  }

  public InstanceImpl(BeanManager beanManager, Class<T> type, Annotation... qualifiers) {
    this.type = type;
    this.beanManager = beanManager;
    this.qualifiers = qualifiers;
  }

  @Override
  public InstanceImpl<T> select(Annotation... annotations) {
    Annotation[] combined = Stream.concat(Arrays.stream(qualifiers), Arrays.stream(annotations))
        .toArray(Annotation[]::new);
    return new InstanceImpl<>(beanManager, type, combined);
  }

  @Override
  public <U extends T> InstanceImpl<U> select(Class<U> subtype, Annotation... annotations) {
    Annotation[] combined = Stream.concat(Arrays.stream(qualifiers), Arrays.stream(annotations))
        .toArray(Annotation[]::new);
    return new InstanceImpl<>(beanManager, subtype, combined);
  }

  @Override
  public boolean isUnsatisfied() {
    if (qualifiers == null || qualifiers.length == 0) {
      return beanManager.lookupBeans(type, QualifierUtil.DEFAULT_ANNOTATION).size() != 1;
    }

    return beanManager.lookupBeans(type, qualifiers).size() != 1;
  }

  @Override
  public boolean isAmbiguous() {
    return beanManager.lookupBeans(type, qualifiers).size() > 1;
  }

  @Override
  public void destroy(T instance) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public Iterator<T> iterator() {
    return new InstanceImpl.InstanceImplIterator<>(beanManager.lookupBeans(type, qualifiers));
  }

  @Override
  public T get() {
    if (qualifiers.length == 0) {
      return beanManager.lookupBean(type, QualifierUtil.DEFAULT_ANNOTATION).getInstance();
    }
    return beanManager.lookupBean(type, qualifiers).getInstance();
  }

  @Override
  public T getInstance() {
    return get();
  }

  private static class InstanceImplIterator<T> implements Iterator<T> {

    private final Iterator<SyncBeanDef<T>> delegate;

    public InstanceImplIterator(final Collection<SyncBeanDef<T>> beans) {
      this.delegate = beans.iterator();
    }

    @Override
    public boolean hasNext() {
      return delegate.hasNext();
    }

    @Override
    public T next() {
      final SyncBeanDef<T> bean = delegate.next();
      return bean.getInstance();
    }
  }

}
