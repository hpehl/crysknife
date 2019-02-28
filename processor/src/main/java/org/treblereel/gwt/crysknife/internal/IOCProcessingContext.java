package org.treblereel.gwt.crysknife.internal;

import org.treblereel.gwt.crysknife.internal.api.InjectableProvider;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/28/19
 */
public class IOCProcessingContext {

    private final Map<WiringElementType, Class<? extends Annotation>> elementBindings = new HashMap<>();

    private final Map<InjectableHandle, InjectableProvider> injectableProviders = new HashMap<>();
    private final Map<InjectableHandle, InjectableProvider> exactTypeInjectableProviders = new HashMap<>();
}
