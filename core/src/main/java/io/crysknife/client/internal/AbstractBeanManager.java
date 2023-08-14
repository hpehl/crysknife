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

import io.crysknife.client.BeanManager;
import io.crysknife.client.IOCBeanDef;
import io.crysknife.client.SyncBeanDef;
import jakarta.enterprise.inject.Typed;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.crysknife.client.internal.QualifierUtil.ANY_ANNOTATION;
import static io.crysknife.client.internal.QualifierUtil.DEFAULT_ANNOTATION;
import static io.crysknife.client.internal.QualifierUtil.DEFAULT_QUALIFIERS;
import static io.crysknife.client.internal.QualifierUtil.SPECIALIZES_ANNOTATION;
import static io.crysknife.client.internal.QualifierUtil.matches;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBeanManager implements BeanManager {

  private final Map<Class, BeanDefinitionHolder> beans = new HashMap<>();

  private final Map<Object, BeanFactory> pool = new HashMap<>();
  private final Map<String, Class> beansByBeanName = new HashMap<>();

  private final Predicate<SyncBeanDefImpl> isTyped =
      syncBeanDef -> syncBeanDef.getTyped().isPresent();
  private final Predicate<SyncBeanDefImpl> isNotTyped =
      syncBeanDef -> syncBeanDef.getTyped().isEmpty();

  private final Predicate<SyncBeanDefImpl> hasFactory =
      syncBeanDef -> syncBeanDef.getFactory().isPresent();

  private final Predicate<SyncBeanDefImpl> hasDefaultQualifiers =
      bean -> bean.matches(setOf(DEFAULT_ANNOTATION));

  protected AbstractBeanManager() {

  }

  public void register(final SyncBeanDefImpl beanDefinition) {
    BeanDefinitionHolder holder =
        beans.computeIfAbsent(beanDefinition.getType(), k -> new BeanDefinitionHolder());
    for (Class<?> superType : (Collection<Class<?>>) beanDefinition.getAssignableTypes()) {
      beans.computeIfAbsent(superType, k -> new BeanDefinitionHolder()).subTypes.add(holder);
      beansByBeanName.put(superType.getCanonicalName(), superType);
    }
    Set<Annotation> temp = new HashSet<Annotation>(beanDefinition.getActualQualifiers());
    holder.qualifiers.put(temp, beanDefinition);
    beansByBeanName.put(beanDefinition.getName(), beanDefinition.getType());
  }

  @Override
  public Collection<SyncBeanDef> lookupBeans(String name) {
    if (beansByBeanName.containsKey(name)) {
      return lookupBeans(beansByBeanName.get(name));
    }
    return Collections.EMPTY_SET;
  }

  public <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type) {
    Set<SyncBeanDef<T>> result = new HashSet<>();
    if (!beans.containsKey(type)) {
      return result;
    }
    of(beans.get(type)).map(f -> (SyncBeanDef<T>) f).forEach(result::add);
    return result;
  }

  private Stream<SyncBeanDefImpl> of(BeanDefinitionHolder holder,
      Predicate<SyncBeanDefImpl>... filters) {
    Stream<SyncBeanDefImpl> stream =
        Stream.of(holder.subTypes.stream().flatMap(f -> f.qualifiers.values().stream()),
            holder.qualifiers.values().stream()).flatMap(Function.identity());
    for (Predicate<SyncBeanDefImpl> filter : filters) {
      stream = stream.filter(filter);
    }

    return stream;
  }

  public <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type, Annotation... qualifiers) {
    Set<SyncBeanDef<T>> result = new HashSet<>();
    if (!beans.containsKey(type)) {
      return result;
    }

    if (qualifiers.length == 0) {
      return lookupBeans(type);
    }

    of(beans.get(type)).filter(bean -> bean.matches(setOf(qualifiers))).map(f -> (SyncBeanDef<T>) f)
        .forEach(result::add);

    return result;
  }

  public <T> SyncBeanDef<T> lookupBean(final Class<T> type) {
    Collection<IOCBeanDef<T>> candidates = doLookupBean(type, QualifierUtil.DEFAULT_ANNOTATION);

    if (candidates.size() > 1) {
      throw BeanManagerUtil.ambiguousResolutionException(type, candidates, DEFAULT_ANNOTATION);
    } else if (candidates.isEmpty()) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, DEFAULT_ANNOTATION);
    } else {
      return (SyncBeanDef<T>) candidates.iterator().next();
    }
  }

  public <T> SyncBeanDef<T> lookupBean(final Class<T> type, Annotation... qualifiers) {
    Collection<IOCBeanDef<T>> candidates = doLookupBean(type, qualifiers);

    if (candidates.size() > 1) {
      throw BeanManagerUtil.ambiguousResolutionException(type, candidates, qualifiers);
    } else if (candidates.isEmpty()) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    } else {
      return (SyncBeanDef<T>) candidates.iterator().next();
    }
  }

  public void destroyBean(Object ref) {
    if (pool.containsKey(ref)) {
      pool.get(ref).onDestroyInternal(ref);
      pool.remove(ref);
    }
  }

  private <T> Collection<IOCBeanDef<T>> doLookupBean(final Class<T> type,
      final Annotation... qualifiers) {
    if (!beans.containsKey(type)) {
      return Collections.EMPTY_SET;
    }

    Collection<IOCBeanDef<T>> candidates = new HashSet<>();
    BeanDefinitionHolder holder = beans.get(type);

    Optional<IOCBeanDef<T>> maybeTyped = of(holder, isTyped)
        .filter(bean -> Arrays.asList(((Typed) bean.getTyped().get()).value()).contains(type))
        .map(bean -> (IOCBeanDef<T>) bean).findFirst();

    if (maybeTyped.isPresent()) {
      return setOf(maybeTyped.get());
    }

    of(holder, hasFactory, isNotTyped).filter(bean -> {
      Set<Annotation> temp = new HashSet<>(bean.getActualQualifiers());
      Collections.addAll(temp, DEFAULT_QUALIFIERS);
      return compareAnnotations(temp, qualifiers);
    }).forEach(bean -> candidates.add((IOCBeanDef<T>) bean));

    if (qualifiers.length == 1 && isDefault(qualifiers)) {
      Optional<IOCBeanDef<T>> maybeSpecialized =
          of(holder, isNotTyped).filter(bean -> bean.matches(setOf(SPECIALIZES_ANNOTATION)))
              .map(bean -> (IOCBeanDef<T>) bean).findFirst();

      // TODO this is not correct, specialized bean totally overrides the parent bean, including
      // qualifiers
      if (maybeSpecialized.isPresent()) {
        return setOf(maybeSpecialized.get());
      }

      Optional<IOCBeanDef<T>> maybeDefault = of(holder, isNotTyped, hasDefaultQualifiers)
          .map(bean -> (IOCBeanDef<T>) bean).findFirst();

      if (maybeDefault.isPresent()) {
        return setOf(maybeDefault.get());
      }

      of(holder, isNotTyped, hasFactory, hasDefaultQualifiers).map(bean -> (IOCBeanDef<T>) bean)
          .forEach(candidates::add);
    } else if (qualifiers.length == 1 && isAny(qualifiers)) {

      of(holder, hasFactory)
          // .filter(bean -> bean.getTyped().isEmpty()) //TODO not sure about this, may I have to
          // filter out @typed beans
          .map(bean -> (IOCBeanDef<T>) bean).forEach(candidates::add);
    } else {
      of(holder, isNotTyped, hasFactory).filter(bean -> bean.matches(setOf(qualifiers)))
          .map(bean -> (IOCBeanDef<T>) bean).forEach(candidates::add);
    }
    return candidates;
  }

  private boolean compareAnnotations(Collection<Annotation> all, Annotation... in) {
    Annotation[] _all = all.toArray(new Annotation[all.size()]);
    return matches(in, _all);
  }

  private boolean isDefault(Annotation[] qualifiers) {
    Annotation[] a1 = new Annotation[] {qualifiers[0]};
    Annotation[] a2 = new Annotation[] {DEFAULT_ANNOTATION};
    return matches(a1, a2);
  }

  private boolean isAny(Annotation[] qualifiers) {
    Annotation[] a1 = new Annotation[] {qualifiers[0]};
    Annotation[] a2 = new Annotation[] {ANY_ANNOTATION};
    return matches(a1, a2);
  }

  private <T> Collection<IOCBeanDef<T>> doLookupBean(final Class<T> type) {
    Collection<IOCBeanDef<T>> candidates = new ArrayList<>();
    if (beans.containsKey(type)) {
      if (!beans.get(type).qualifiers.isEmpty()) {
        beans.get(type).qualifiers.values().forEach(candidates::add);
      } else {
        of(beans.get(type), isNotTyped, hasFactory).filter(syncBeanDef -> {
          if (syncBeanDef.getActualQualifiers().isEmpty()) {
            return true;
          } else {
            return syncBeanDef.matches(setOf(DEFAULT_ANNOTATION));
          }
        }).map(bean -> (IOCBeanDef<T>) bean).forEach(candidates::add);
      }
    }
    return candidates;
  }

  // replace it with setOf right after we move to Java 11 emulated by J2CL
  public static <T> Set<T> setOf(T... values) {
    Set<T> set = new HashSet<>();
    Collections.addAll(set, values);
    return Collections.unmodifiableSet(set);
  }

  <T> T addBeanInstanceToPool(Object instance, BeanFactory<T> factory) {
    pool.put(instance, factory);
    return (T) instance;
  }

  private static class BeanDefinitionHolder {

    private final Set<BeanDefinitionHolder> subTypes = new HashSet<>();
    private final Map<Set<Annotation>, SyncBeanDefImpl> qualifiers = new HashMap<>();

  }
}
