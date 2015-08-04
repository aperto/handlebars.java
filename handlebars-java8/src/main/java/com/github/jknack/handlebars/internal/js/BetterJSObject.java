package com.github.jknack.handlebars.internal.js;

import static com.github.jknack.handlebars.internal.js.JavaObjectToJSTranslation.needsTranslation;
import static com.github.jknack.handlebars.internal.js.JavaObjectToJSTranslation.translate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jfrantzius
 * @see {@link https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions#Nashornextensions-Plugging-inyourownJSObject} 
 */
public class BetterJSObject extends jdk.nashorn.api.scripting.AbstractJSObject {

  /**
   * Whether we want to behave like a JS array.
   */
  private boolean isArray;

  /**
   * For Map input: same as original map.
   * For collection or array input: input keyed by index.
   */
  @SuppressWarnings("rawtypes")
  private Map state;

  /**
   * A JS array.
   *
   * @param array Array.
   */
  @SuppressWarnings("unchecked")
  public BetterJSObject(final Object[] array) {
      state = new LinkedHashMap<Integer, Object>(); // try to preserve order of entries
      for (int i = 0; i < array.length; i++) {
        state.put(i, array[i]);
      }
      isArray = true;
  }

  /**
   * Reflect a Map.
   * @param map to reflect
   */
  public BetterJSObject(final Map<?, ?> map) {
      state = map;
      isArray = false;
  }

  /**
   * Reflect a collection.
   *
   * @param collection collection.
   */
  public BetterJSObject(final Collection<Object> collection) {
    this(collection.toArray(new Object[collection.size()]));
  }

  /**
   * Translate if required and replace in state.
   * @param key within state map.
   * @return translated or same
   */
  @SuppressWarnings("unchecked")
  private Object translateAndReplaceIfRequired(final Object key) {
    Object value = state.get(key);
    // replace with translated object if necessary
    if (needsTranslation(value)) {
        value = translate(value);
        state.put(key, value);
    }
    return value;
  }


  /**
   * Retrieves a named member of this JavaScript object.
   *
   * @param name of member
   * @return member
   */
  @Override
  public Object getMember(final String name) {
      return state.get(name);
  }

  /**
   * Retrieves an indexed member of this JavaScript object.
   *
   * @param index index slot to retrieve
   * @return member
   */
  @Override
  public Object getSlot(final int index) {
      return state.get(index);
  }

  /**
   * Does this object have a named member?
   *
   * @param name name of member
   * @return true if this object has a member of the given name
   */
  @Override
  public boolean hasMember(final String name) {
      return state.containsKey(name);
  }

  /**
   * Does this object have a indexed property?
   *
   * @param slot index to check
   * @return true if this object has a slot
   */
  @Override
  public boolean hasSlot(final int slot) {
      return state.containsKey(slot);
  }

  /**
   * Remove a named member from this JavaScript object.
   *
   * @param name name of the member
   */
  @Override
  public void removeMember(final String name) {
      state.remove(name);
  }

  /**
   * Set a named member in this JavaScript object.
   *
   * @param name  name of the member
   * @param value value of the member
   */
  @Override
  public void setMember(final String name, final Object value) {
      state.put(name, value);
  }

  /**
   * Set an indexed member in this JavaScript object.
   *
   * @param index index of the member slot
   * @param value value of the member
   */
  @Override
  public void setSlot(final int index, final Object value) {
      state.put(index, value);
  }

  // property and value iteration

  /**
   * Returns the set of all property names of this object.
   *
   * @return set of property names
   */
  @Override
  @SuppressWarnings("unchecked")
  public Set<String> keySet() {
      return state.keySet();
  }

  /**
   * Returns the set of all property values of this object.
   *
   * @return set of property values.
   */
  @Override
  @SuppressWarnings("unchecked")
  public Collection<Object> values() {
      return state.values();
  }

}
