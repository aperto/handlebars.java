/**
 * Copyright (c) 2012-2013 Edgar Espina
 *
 * This file is part of Handlebars.java.
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
package com.github.jknack.handlebars.internal.js;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.script.ScriptEngine;

import org.slf4j.Logger;

import jdk.nashorn.internal.runtime.ScriptObject;

/**
 * Turn Java objects into handy JS objects that e.g. support property access.
 * Since NativeArray etc. are final classes, must do so all at once
 * for the whole given object graph, not on-demand.
 * @author jfrantzius
 */
@SuppressWarnings("serial")
final class JavaObjectToJSTranslation {

  /**
   * The logging system.
   */
  private static final Logger logger = getLogger(JavaObjectToJSTranslation.class);


  /**
   * private constructor because checkstyle asks for it.
   */
  private JavaObjectToJSTranslation() {
  }

  /**
   * Populate new ScriptObject with properties from Map.
   * @param map to take properties from
   * @return Scriptobject
   */
  private static ScriptObject translateMap(final Map<?, ?> map) {
    ScriptObject scriptObject = jdk.nashorn.internal.runtime.Context.getGlobal().newObject();
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      scriptObject.put(translateIfNecessary(entry.getKey()),
          translateIfNecessary(entry.getValue()), false);
    }
    return scriptObject;
  }

  /**
   * Translate Collection to NativeArray.
   * @param collection to translate
   * @return NativeArray
   */
  private static jdk.nashorn.internal.objects.NativeArray translateCollection(
      final Collection<?> collection) {
    Object[] array = new Object[collection.size()];
    Iterator iter = collection.iterator();
    for (int i = 0; i < collection.size(); i++) {
      array[i] = translateIfNecessary(iter.next());
    }
    return (jdk.nashorn.internal.objects.NativeArray)
        jdk.nashorn.internal.runtime.Context.getGlobal().wrapAsObject(array);
  }

  /**
   * Translate by augmenting Global.wrapAsObject() with special treatment of
   * Collections and Maps.
   * @param object to be translated
   * @return the translated object, or same
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  static Object translate(final Object object) {
      if (Map.class.isInstance(object)) {
          return translateMap((Map) object);
      } else if (Collection.class.isInstance(object)) {
          return translateCollection((Collection) object);
      } else {
          return jdk.nashorn.internal.runtime.Context.getGlobal().wrapAsObject(object);
      }
  }

  /**
   * Whether given object needs to be translated.
   * @param object in question
   * @return whether needs translation
   */
  static boolean needsTranslation(final Object object) {
    return object != null
        && (Map.class.isInstance(object)
            || Collection.class.isInstance(object));
  }

  /**
   * Translate object if necessary, or return same.
   * @param object in question
   * @return translated or same
   */
  public static Object translateIfNecessary(final Object object) {
    if (needsTranslation(object)) {
      return translate(object);
    } else {
      return object;
    }
  }
}
