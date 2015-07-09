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

import java.util.ArrayList;
import java.util.Collection;

import org.mozilla.javascript.Scriptable;

/**
 * Converts any Maps or Collections passed into constructor into
 * {@link ScriptableMap} or ScriptableCollection.
 *
 * This will happen recursively.
 *
 * @author jfrantzius
 *
 */
public class ScriptableCollection extends ArrayList<Object>implements Scriptable {

  /**
   * Keep Checkstyle happy.
   */
  private static final long serialVersionUID = 4064614950912216638L;

  /**
   * @param collection to convert
   */
  public ScriptableCollection(final Collection<Object> collection) {
    super(collection.size());
    for (Object object : collection) {
      add(JsConversion.toJsObject(object));
    }
  }

  /**
   * @param array to convert
   */
  public ScriptableCollection(final Object[] array) {
    super(array.length);
    for (Object object : array) {
      add(JsConversion.toJsObject(object));
    }
  }

  @Override
  public String getClassName() {
    return this.getClass().getName();
  }

  @Override
  public Object get(final String name, final Scriptable start) {
    if ("length".equals(name)) {
      return this.size();
    }
    throw new UnsupportedOperationException("Property not supported: " + name);
  }

  @Override
  public Object get(final int index, final Scriptable start) {
    return get(index);
  }

  @Override
  public boolean has(final String name, final Scriptable start) {
    throw new UnsupportedOperationException("Access by name not supported on JS array");
  }

  @Override
  public boolean has(final int index, final Scriptable start) {
    return get(index) != null;
  }

  @Override
  public void put(final String name, final Scriptable start, final Object value) {
    throw new UnsupportedOperationException("Access by name not supported on JS array");
  }

  @Override
  public void put(final int index, final Scriptable start, final Object value) {
    set(index, value);
  }

  @Override
  public void delete(final String name) {
    throw new UnsupportedOperationException("Access by name not supported on JS array");
  }

  @Override
  public void delete(final int index) {
    remove(index);
  }

  @Override
  public Scriptable getPrototype() {
    return null;
  }

  @Override
  public void setPrototype(final Scriptable prototype) {
  }

  @Override
  public Scriptable getParentScope() {
    return null;
  }

  @Override
  public void setParentScope(final Scriptable parent) {

  }

  @Override
  public Object[] getIds() {
    return null;
  }

  @Override
  public Object getDefaultValue(final Class<?> hint) {
    return null;
  }

  @Override
  public boolean hasInstance(final Scriptable instance) {
    return false;
  }
}
