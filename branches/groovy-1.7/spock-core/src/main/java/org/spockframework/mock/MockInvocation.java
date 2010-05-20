/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.mock;

import java.lang.reflect.Method;
import java.util.*;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import org.spockframework.util.TextUtil;

/**
 *
 * @author Peter Niederwieser
 */
public class MockInvocation implements IMockInvocation {
  private final IMockObject mockObject;
  private final Method method;
  private final List<Object> arguments;

  public MockInvocation(IMockObject mockObject, Method method, List<Object> arguments) {
    this.mockObject = mockObject;
    this.method = method;
    this.arguments = arguments;
  }

  public IMockObject getMockObject() {
    return mockObject;
  }

  public Method getMethod() {
    return method;
  }

  public List<Object> getArguments() {
    return arguments;
  }

  @Override
  public String toString() {
    String mockName = mockObject.getName();
    if (mockName == null) mockName = String.format("<%s>", mockObject.getType().getSimpleName());
    return String.format("%s.%s(%s)", mockName, method.getName(), render(arguments));
  }

  private String render(List<Object> arguments) {
    List<String> strings = new ArrayList<String>();
    for (Object arg : arguments) strings.add(DefaultGroovyMethods.inspect(arg));
    return TextUtil.join(strings, ", ");
  }
}
