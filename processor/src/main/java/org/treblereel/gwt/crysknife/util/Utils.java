package org.treblereel.gwt.crysknife.util;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

import com.google.auto.common.MoreElements;
import jsinterop.annotations.JsProperty;
import org.treblereel.gwt.crysknife.exception.GenerationException;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class Utils {

    private Utils() {

    }

    public static String getQualifiedFactoryName(TypeElement bean) {
        return getPackageName(bean) + "." + getFactoryClassName(bean);
    }

    public static String getPackageName(TypeElement singleton) {
        return MoreElements.getPackage(singleton).getQualifiedName().toString();
    }

    public static String getFactoryClassName(TypeElement bean) {
        return bean.getSimpleName().toString() + "_Factory";
    }

    public static String getJsFieldName(VariableElement field) {
        if (field.getAnnotation(JsProperty.class) != null) {
            return field.getSimpleName().toString();
        }
        StringBuffer sb = new StringBuffer();
        sb.append("f_");
        sb.append(field.getSimpleName().toString());
        sb.append("__");
        sb.append(MoreElements.asType(field.getEnclosingElement())
                          .getQualifiedName()
                          .toString()
                          .replaceAll("\\.", "_"));
        if (field.getModifiers().contains(Modifier.PRIVATE)) {
            sb.append("_");
        }

        return sb.toString();
    }

    public static Set<Element> getAnnotatedElements(
            Elements elements,
            TypeElement type,
            Class<? extends Annotation> annotation) {
        Set<Element> found = new HashSet<>();
        for (Element e : elements.getAllMembers(type)) {
            if (e.getAnnotation(annotation) != null) {
                found.add(e);
            }
        }
        return found;
    }

    public static String toVariableName(TypeElement injection) {
        return toVariableName(getQualifiedName(injection));
    }

    public static String toVariableName(String name) {
        return name.toLowerCase().replaceAll("\\.", "_");
    }

    public static String getQualifiedName(Element elm) {
        if (elm.getKind().equals(ElementKind.FIELD) || elm.getKind().equals(ElementKind.PARAMETER)) {
            VariableElement variableElement = MoreElements.asVariable(elm);
            DeclaredType declaredType = (DeclaredType) variableElement.asType();
            return declaredType.toString();
        } else if (elm.getKind().equals(ElementKind.CONSTRUCTOR)) {
            ExecutableElement executableElement = MoreElements.asExecutable(elm);
            return executableElement.getEnclosingElement().toString();
        } else if (elm.getKind().isClass() || elm.getKind().isInterface()) {
            TypeElement typeElement = MoreElements.asType(elm);
            return typeElement.getQualifiedName().toString();
        }
        throw new GenerationException("Unable to process bean " + elm.toString());
    }
}
