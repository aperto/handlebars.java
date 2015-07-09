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

import java.util.Collection;
import java.util.Map;

import org.mozilla.javascript.Scriptable;

/**
 * Convert Java objects to counterparts that are more easily accessible through
 * Javascript. Conversion happens recursively for collections and maps (not on demand).
 *
 * Your mileage may vary when trying to modify resulting objects in JS.
 *
 * @author jfrantzius
 *
 */
public final class JsConversion {

  /**
   * keep checkstyle happy.
   */
  private JsConversion() {
  }

  /**
   * Convert a Java Object to Js Object if necessary.
   *
   * @param object
   *        Source object.
   *        Handlebars context.
   * @return A Rhino js object.
   */
  @SuppressWarnings({"unchecked", "rawtypes" })
  static Object toJsObject(final Object object) {
    if (object == null) {
      return null;
    }
    if (object == Scriptable.NOT_FOUND) {
      return Scriptable.NOT_FOUND;
    }
    if (object instanceof Number) {
      return object;
    }
    if (object instanceof Boolean) {
      return object;
    }
    if (object instanceof CharSequence || object instanceof Character) {
      return object.toString();
    }
    if (object instanceof Scriptable) {
      return object;
    }

    if (Map.class.isInstance(object)) {
      return new ScriptableMap((Map) object);
    } else if (Collection.class.isInstance(object)) {
      return new ScriptableCollection((Collection) object);
    } else if (object.getClass().isArray()) {
      return new ScriptableCollection((Object[]) object);
    }
    return object;
  }

}
