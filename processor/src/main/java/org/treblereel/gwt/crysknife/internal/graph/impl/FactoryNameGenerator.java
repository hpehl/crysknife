package org.treblereel.gwt.crysknife.internal.graph.impl;

import org.treblereel.gwt.crysknife.internal.InjectableHandle;
import org.treblereel.gwt.crysknife.internal.InjectableType;
import org.treblereel.gwt.crysknife.internal.api.Qualifier;

import javax.lang.model.element.TypeElement;

import static org.treblereel.gwt.crysknife.utils.GeneratedNamesUtil.qualifiedClassNameToIdentifier;

public class FactoryNameGenerator {

    public String generateFor(final InjectableHandle handle, final InjectableType injectableType) {
        return generateFor(handle.getType(), handle.getQualifier(), injectableType);
    }

    public String generateFor(final TypeElement type, final Qualifier qualifier, final InjectableType injectableType) {
        final String typeName = qualifiedClassNameToIdentifier(type);
        final String qualNames = qualifier.getIdentifierSafeString();
        String factoryName = injectableType + "_factory_for__" + typeName + "__with_qualifiers__" + qualNames;
        return factoryName;
    }
}
