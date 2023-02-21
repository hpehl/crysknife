/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.generator.steps;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.GenerationUtils;

public interface BeanLookupCallGenerator {

  default Expression generate(ClassBuilder clazz, IOCContext context,
      InjectableVariableDefinition fieldPoint) {
    GenerationUtils generationUtils = new GenerationUtils(context);
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    MethodCallExpr callForProducer = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(new NameExpr(typeQualifiedName), "class"));
    return callForProducer;
  }

}
