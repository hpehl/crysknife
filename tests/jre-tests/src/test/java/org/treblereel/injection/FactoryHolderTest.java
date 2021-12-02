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

package org.treblereel.injection;

import org.treblereel.AbstractTest;
import org.treblereel.injection.inheritance.factories.FactoryHolder;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/24/21
 */
public class FactoryHolderTest extends AbstractTest {


  // @Test
  public void beanManagertest() {
    FactoryHolder factoryHolder =
        (FactoryHolder) app.beanManager.lookupBean(FactoryHolder.class).getInstance();



    assertEquals(FactoryHolder.class, factoryHolder.getClass());
  }
}