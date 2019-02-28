package org.treblereel.gwt.crysknife.internal.api;

import org.treblereel.gwt.crysknife.internal.InjectionSite;
import org.treblereel.gwt.crysknife.internal.api.Injectable;
import org.treblereel.gwt.crysknife.internal.graph.impl.FactoryNameGenerator;

public interface InjectableProvider {

  /**
   * @param injectionSite Metadata for an injection site.
   * @param nameGenerator TODO
   * @return A {@link Injectable} for the given injeciton site.
   */
  CustomFactoryInjectable getInjectable(InjectionSite injectionSite, FactoryNameGenerator nameGenerator);

}
