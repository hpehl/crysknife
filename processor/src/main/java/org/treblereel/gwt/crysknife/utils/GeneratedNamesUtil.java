package org.treblereel.gwt.crysknife.utils;

import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/28/19
 */
public class GeneratedNamesUtil {

    public static String qualifiedClassNameToIdentifier(final TypeElement type) {
        return qualifiedClassNameToIdentifier(type.getQualifiedName().toString());
    }

    public static String qualifiedClassNameToIdentifier(final String fullyQualifiedName) {
        return fullyQualifiedName.replace('.', '_').replace('$', '_');
    }
}
