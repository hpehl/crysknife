package org.treblereel.gwt.crysknife;

import java.io.IOException;
import java.io.PrintWriter;

import javax.tools.JavaFileObject;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.treblereel.gwt.crysknife.client.Reflect;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 1/1/20
 */
public class BeanInfoGenerator {

    private final IOCContext iocContext;

    private final GenerationContext generationContext;

    public BeanInfoGenerator(IOCContext iocContext, GenerationContext generationContext) {
        this.iocContext = iocContext;
        this.generationContext = generationContext;
    }

    public void generate() {
        iocContext.getBeans().forEach((k, bean) -> {
            try {
                build(bean);
            } catch (IOException e) {
                throw new Error(e);
            }
        });
    }

    private void build(BeanDefinition bean) throws IOException {
        JavaFileObject builderFile = generationContext.getProcessingEnvironment().getFiler()
                .createSourceFile(bean.getQualifiedName() + "Info");
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.append(generate(bean));
        }
    }

    private String generate(BeanDefinition bean) {
        if (generationContext.isGwt2()) {
            return new BeanInfoGenerator.BeanInfoGWT2GeneratorBuilder(bean).build();
        } else {
            return new BeanInfoGenerator.BeanInfoGeneratorBuilder(bean).build().toString();
        }
    }

    private class BeanInfoGWT2GeneratorBuilder {

        private final BeanDefinition bean;
        private final StringBuilder clazz = new StringBuilder();
        private final String newLine = System.lineSeparator();

        public BeanInfoGWT2GeneratorBuilder(BeanDefinition bean) {
            this.bean = bean;
        }

        public String build() {
            initClass();
            addFields();
            return clazz.append(newLine).append("}").toString();
        }

        private void initClass() {
            clazz.append("package ").append(bean.getPackageName()).append(";");
            clazz.append(newLine);
            clazz.append("class ").append(bean.getClassName())
                    .append("Info").append(" {");
        }

        private void addFields() {
            for (FieldPoint fieldPoint : bean.getFieldInjectionPoints()) {
                clazz.append(newLine);
                makeSetter(fieldPoint);
                clazz.append(newLine);
                makeGetter(fieldPoint);
            }
        }

        private void makeSetter(FieldPoint fieldPoint) {
            clazz.append("static native void ")
                    .append(fieldPoint.getName())
                    .append("(");
            clazz.append(bean.getClassName()).append(" ").append(" instance").append(",");
            clazz.append("Object").append(" ").append(" value").append(")/*-{");
            clazz.append(newLine);

            clazz.append("    ")
                    .append("instance.@").append(bean.getQualifiedName())
                    .append("::").append(fieldPoint.getName()).append("=").append("value;");

            clazz.append(newLine).append("}-*/;");
        }

        private void makeGetter(FieldPoint fieldPoint) {
            clazz.append("static native ")
                    .append(fieldPoint.getType().getQualifiedName().toString())
                    .append(" ")
                    .append(fieldPoint.getName())
                    .append("(");
            clazz.append(bean.getClassName()).append(" ").append(" instance");
            clazz.append(")/*-{");
            clazz.append(newLine);

            clazz.append("    ")
                    .append("return instance.@").append(bean.getQualifiedName())
                    .append("::").append(fieldPoint.getName())
                    .append(";");

            clazz.append(newLine).append("}-*/;");
        }
    }

    private class BeanInfoGeneratorBuilder {

        private final BeanDefinition bean;
        private CompilationUnit clazz = new CompilationUnit();
        private ClassOrInterfaceDeclaration classDeclaration;

        public BeanInfoGeneratorBuilder(BeanDefinition bean) {
            this.bean = bean;
        }

        public CompilationUnit build() {
            initClass();
            addFields();
            return clazz;
        }

        private void initClass() {
            clazz.setPackageDeclaration(bean.getPackageName());
            classDeclaration = clazz.addClass(bean.getClassName() + "Info");
            clazz.addImport(Reflect.class);
        }

        private void addFields() {
            for (FieldPoint fieldPoint : bean.getFieldInjectionPoints()) {
                classDeclaration.addFieldWithInitializer(String.class,
                                                         fieldPoint.getName(),
                                                         new StringLiteralExpr(Utils.getJsFieldName(fieldPoint.getField())),
                                                         Modifier.Keyword.PUBLIC,
                                                         Modifier.Keyword.FINAL,
                                                         Modifier.Keyword.STATIC);
            }
        }
    }
}
