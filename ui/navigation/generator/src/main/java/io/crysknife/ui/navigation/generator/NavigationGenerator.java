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

package io.crysknife.ui.navigation.generator;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.google.auto.common.MoreElements;
import io.crysknife.annotation.Generator;
import io.crysknife.client.BeanManager;
import io.crysknife.generator.SingletonGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.PrintWriterTreeLogger;
import io.crysknife.nextstep.definition.BeanDefinition;
import io.crysknife.ui.navigation.client.local.Navigation;
import io.crysknife.ui.navigation.client.local.Page;
import io.crysknife.ui.navigation.client.local.spi.NavigationGraph;
import io.crysknife.ui.navigation.client.shared.NavigationEvent;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/1/20
 */
@Generator
public class NavigationGenerator extends SingletonGenerator {

  public NavigationGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, NavigationGraph.class, WiringElementType.BEAN, this);
  }

  @Override
  public void before() {

    Set<TypeElement> pages =
        iocContext.getGenerationContext().getRoundEnvironment().getElementsAnnotatedWith(Page.class)
            .stream().filter(elm -> elm.getKind().equals(ElementKind.CLASS))
            .map(elm -> MoreElements.asType(elm)).collect(Collectors.toSet());

    /*
     * TypeElement type = iocContext.getGenerationContext().getElements()
     * .getTypeElement(Navigation.class.getCanonicalName()); BeanDefinition navigation =
     * iocContext.getBeanDefinitionOrCreateAndReturn(type);
     * 
     * pages.forEach(elm -> { BeanDefinition page =
     * iocContext.getBeanDefinitionOrCreateAndReturn(elm); navigation.getDependsOn().add(page); });
     * 
     * new NavigationGraphGenerator(pages).generate(new PrintWriterTreeLogger(),
     * iocContext.getGenerationContext());
     */
  }

  @Override
  protected ObjectCreationExpr generateNewInstanceCreationExpr(BeanDefinition definition) {
    ObjectCreationExpr newInstance = new ObjectCreationExpr();
    return newInstance
        .setType(NavigationGraph.class.getPackage().getName() + ".GeneratedNavigationGraph")
        .addArgument(new MethodCallExpr(
            new NameExpr(BeanManager.class.getPackage().getName() + ".BeanManagerImpl"), "get"))
        .addArgument(new MethodCallExpr(
            new MethodCallExpr(new NameExpr("javax.enterprise.event.Event_Factory"), "get"), "get")
                .addArgument(NavigationEvent.class.getCanonicalName() + ".class"));
  }
}
