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

package org.spockframework.tapestry;

import org.apache.tapestry5.ioc.annotations.SubModule;

import org.spockframework.runtime.extension.ISpockExtension;
import org.spockframework.runtime.intercept.IMethodInterceptor;
import org.spockframework.runtime.model.SpecInfo;

/**
 * Facilitates the creation of integration-level specifications for applications based
 * on the Tapestry 5 inversion-of-control container. The main focus is on injecting
 * specifications with Tapestry-provided objects. This works just like for
 * regular Tapestry services, except that only field (and no constructor)
 * injection is supported.
 *
 * <p>Instead of inventing its own set of annotations, this extension reuses
 * Tapestry's own annotations. In particular,
 *
 * <ul>
 * <li><tt>&#64;SubModule</tt> indicates which Tapestry module(s) should be started
 *  (and subsequently shut down)</li>
 * <li><tt>&#64;Inject</tt> marks fields which should be injected with a Tapestry service or
 * symbol</li>
 * </ul>
 *
 * Related Tapestry annotations, such as <tt>&#64;Service</tt> and <tt>&#64;Symbol</tt>,
 * are also supported. For information on their use, see the
 * <a href="http://tapestry.apache.org/tapestry5/tapestry-ioc/">Tapestry IoC documentation</a>.
 * To interact directly with the Tapestry registry, an injection point of type
 * <tt>ObjectLocator</tt> may be defined. However, this should be rarely needed.
 *
 * <p>For every specification annotated with <tt>&#64;SubModule</tt>, the Tapestry
 * registry will be started up (and subsequently shut down) once. Because fields are injected
 * <em>before</em> field initializers and the <tt>setup()</tt>/<tt>setupSpec()</tt>
 * methods are run, they can be safely accessed from these places.
 *
 * <p>Fields marked as <tt>&#64;Shared</tt> are injected once per specification; regular 
 * fields once per feature (iteration). However, this does <em>not</em> mean that each
 * feature will receive a fresh service instance; rather, it is left to the Tapestry
 * registry to control the lifecycle of a service. Most Tapestry services use the default
 * "singleton" scope, which results in the same service instance being shared between all
 * features.
 *
 * <p>Features that require their own service instance(s) should be moved into separate
 * specifications. To avoid code fragmentation and duplication, you might want to put
 * multiple (micro-)specifications into the same source file, and factor out their
 * commonalities into a base class. Alternatively, a service definition in a
 * specification's own module may use <tt>&#64;Scope("perIteration")</tt>. In this case,
 * every feature (iteration) will receive a fresh service instance.
 *
 * <p><b>Usage example:</b>
 *
 * <pre>
 * &#64;SubModule(UniverseModule)
 * class UniverseSpec extends Specification {
 *   &#64;Inject
 *   UniverseService service
 *
 *   UniverseService copy = service
 *
 *   def "service knows the answer to the universe"() {
 *     expect:
 *     copy == service        // injection occurred before 'copy' was initialized
 *     service.answer() == 42 // what else did you expect?!
 *   }
 * }
 * </pre>
 *
 * @author Peter Niederwieser
 */
public class TapestryExtension implements ISpockExtension {
  public void visitSpec(SpecInfo spec) {
    if (!spec.getReflection().isAnnotationPresent(SubModule.class)) return;

    IMethodInterceptor interceptor = new TapestryInterceptor(spec);
    spec.getSetupSpecMethod().addInterceptor(interceptor);
    spec.getSetupMethod().addInterceptor(interceptor);
    spec.getCleanupMethod().addInterceptor(interceptor);
    spec.getCleanupSpecMethod().addInterceptor(interceptor);
  }
}
