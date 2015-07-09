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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import org.mozilla.javascript.Scriptable;

/**
 * Make a map's entries accessible using dot-notation in javascript,
 * e.g. "m.foo" instead of "m.get('foo')".
 *
 * Originally taken from http://stackoverflow.com/a/7605787/1245428 ,
 * adding correct generics, throwing {@link UnsupportedOperationException} on
 * index access, and using {@link LinkedHashMap} to preserve order
 * of entries.
 *
 * @author jfrantzius
 *
 */
public class ScriptableMap implements Scriptable, Map<String, Object>, Bindings {

  /**
   * The map delegate.
   */
  private final Map<String, Object> map;

  /**
   * @param map to convert
   */
  public ScriptableMap(final Map<String, Object> map) {
    this.map = new LinkedHashMap<String, Object>(map);
    for (java.util.Map.Entry<String, Object> entry : map.entrySet()) {
      map.put(entry.getKey(), JsConversion.toJsObject(entry.getValue()));
    }
  }

  /**
   * @see java.util.Map#clear()
   */
  public void clear() {
    map.clear();
  }

  /**
   * @see java.util.Map#containsKey(java.lang.Object)
   * @param key to look for
   * @return delegate result
   */
  public boolean containsKey(final Object key) {
    return map.containsKey(key);
  }

  /**
   * @see java.util.Map#containsValue(java.lang.Object)
   * @param value to look for
   * @return delegate result
   */
  public boolean containsValue(final Object value) {
    return map.containsValue(value);
  }

  /**
   * @see java.util.Map#entrySet()
   * @return delegate result
   */
  public Set<Map.Entry<String, Object>> entrySet() {
    return map.entrySet();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   * @param o to compare
   * @return delegate result
   */
  public boolean equals(final Object o) {
    return map.equals(o);
  }

  /**
   * @see java.util.Map#get(java.lang.Object)
   * @param key to look for
   * @return delegate result
   */
  public Object get(final Object key) {
    return map.get(key);
  }

  /**
   * @see java.lang.Object#hashCode()
   * @return delegate result
   */
  public int hashCode() {
    return map.hashCode();
  }

  /**
   * @see java.util.Map#isEmpty()
   * @return delegate result
   */
  public boolean isEmpty() {
    return map.isEmpty();
  }

  /**
   * @see java.util.Map#keySet()
   * @return delegate result
   */
  public Set<String> keySet() {
    return map.keySet();
  }

  /**
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   * @param key to put
   * @param value to put
   * @return delegate result
   */
  public Object put(final String key, final Object value) {
    return map.put(key, value);
  }

  /**
   * @see java.util.Map#putAll(java.util.Map)
   * @param m the map
   */
  public void putAll(final Map<? extends String, ? extends Object> m) {
    map.putAll(m);
  }

  /**
   * @see java.util.Map#remove(java.lang.Object)
   * @param key the key
   * @return delegate result
   */
  public Object remove(final Object key) {
    return map.remove(key);
  }

  /**
   * @see java.util.Map#size()
   * @return delegate result
   */
  public int size() {
    return map.size();
  }

  /**
   * @see java.util.Map#values()
   * @return delegate result
   */
  public Collection<Object> values() {
    return map.values();
  }

  @Override
  public void delete(final String name) {
    map.remove(name);
  }

  @Override
  public void delete(final int index) {
    map.remove(index);
  }

  @Override
  public Object get(final String name, final Scriptable start) {
    return map.get(name);
  }

  @Override
  public Object get(final int index, final Scriptable start) {
    throw new UnsupportedOperationException("Access by index not supported");
  }

  @Override
  public String getClassName() {
    return map.getClass().getName();
  }

  @Override
  public Object getDefaultValue(final Class<?> hint) {
    return toString();
  }

  @Override
  public Object[] getIds() {
    Object[] res = new Object[map.size()];
    int i = 0;
    for (Object k : map.keySet()) {
      res[i] = k;
      i++;
    }
    return res;
  }

  @Override
  public Scriptable getParentScope() {
    return null;
  }

  @Override
  public Scriptable getPrototype() {
    return null;
  }

  @Override
  public boolean has(final String name, final Scriptable start) {
    return map.containsKey(name);
  }

  @Override
  public boolean has(final int index, final Scriptable start) {
    throw new UnsupportedOperationException("Access by index not supported");
  }

  @Override
  public boolean hasInstance(final Scriptable instance) {
    return false;
  }

  @Override
  public void put(final String name, final Scriptable start, final Object value) {
    map.put(name, value);
  }

  @Override
  public void put(final int index, final Scriptable start, final Object value) {
    throw new UnsupportedOperationException("Access by index not supported");
  }

  @Override
  public void setParentScope(final Scriptable parent) {
  }

  @Override
  public void setPrototype(final Scriptable prototype) {
  }
}
