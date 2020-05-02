/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.scala.test;

import _root_.scala.reflect.BeanProperty

/**
 * Just a simple Animal test class
 */
abstract class Animal(@BeanProperty val name: String, @BeanProperty val genus: String)

case class Cat(override val name: String) extends Animal(name, "felis") 
case class Kitten(override val name: String) extends Cat(name)

case class Dog(override val name: String) extends Animal(name, "canis")
case class Puppy(override val name: String) extends Dog(name)
