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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.internal.Files;
import com.github.jknack.handlebars.js.HandlebarsJs;

/**
 * An implementation of {@link HandlebarsJs} on top of Rhino.
 *
 * @author edgar.espina.
 * @since 1.1.0
 */
public class RhinoHandlebars extends HandlebarsJs {

  /**
   * The JavaScript helper contract.
   *
   * @author edgar.espina
   * @since 1.1.0
   */
  public interface JsHelper {

    /**
     * Apply the helper to the context.
     *
     * @param context The context object.
     * @param arg0 The helper first argument.
     * @param options The options object.
     * @return A string result.
     */
    Object apply(Object context, Object arg0, OptionsJs options);
  }

  /**
   * The Handlebars.js options.
   *
   * @author edgar.espina
   * @since 1.1.0
   */
  public static class OptionsJs {
    /**
     * Handlebars.java options.
     */
    private Options options;

    /**
     * Accessed from Javascript.
     */
    public final Map<String, Object> hash;

    /**
     * Accessed from Javascript.
     */
    public final Object[] params;

    /**
     * Creates a new {@link HandlebarsJs} options.
     *
     * @param options The {@link HandlebarsJs} options.
     */
    public OptionsJs(final Options options) {
      this.options = options;
      this.hash = new ScriptableMap(options.hash);
      this.params = options.params;
    }

    /**
     * Apply the {@link #options#fn(Object)} template using the provided context.
     *
     * @param context The context to use.
     * @return The resulting text.
     * @throws IOException If a resource cannot be loaded.
     */
    public CharSequence fn(final Object context) throws IOException {
      return options.fn(context);
    }

    /**
     * Apply the {@link #options#inverse(Object)} template using the provided context.
     *
     * @param context The context to use.
     * @return The resulting text.
     * @throws IOException If a resource cannot be loaded.
     */
    public CharSequence inverse(final Object context) throws IOException {
      return options.inverse(context);
    }
  }

  /**
   * The JavaScript helpers environment for Rhino.
   */
  private static final String HELPERS_ENV = envSource("/helpers.rhino.js");

  /**
   * Creates a new {@link RhinoHandlebars}.
   *
   * @param helperRegistry The handlebars object.
   */
  public RhinoHandlebars(final HelperRegistry helperRegistry) {
    super(helperRegistry);
  }

  /**
   * Register a helper in the helper registry.
   *
   * @param name The helper's name. Required.
   * @param helper The helper object. Required.
   */
  public void registerHelper(final String name, final JsHelper helper) {
    registry.registerHelper(name, new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options) throws IOException {
        Object jsContext = toScriptableMap(options.context);
        Object arg0 = context;
        Integer paramSize = options.data(Context.PARAM_SIZE);
        if (paramSize == 0) {
          arg0 = "___NOT_SET_";
        }  else {
            arg0 = JsConversion.toJsObject(context);
        }
        Object result = helper.apply(jsContext, arg0, new OptionsJs(options));
        if (result instanceof CharSequence) {
          return (CharSequence) result;
        }
        return result == null ? null : result.toString();
      }
    });
  }

  @Override
  public void registerHelpers(final String filename, final String source) throws Exception {

    org.mozilla.javascript.Context ctx = null;
    try {
      ctx = newContext();

      Scriptable sharedScope = helpersEnvScope(ctx);
      Scriptable scope = ctx.newObject(sharedScope);
      scope.setParentScope(null);
      scope.setPrototype(sharedScope);

      ctx.evaluateString(scope, source, filename, 1, null);
    } finally {
      if (ctx != null) {
        org.mozilla.javascript.Context.exit();
      }
    }
  }

  /**
   * Creates a new Rhino Context.
   *
   * @return A Rhino Context.
   */
  private org.mozilla.javascript.Context newContext() {
    org.mozilla.javascript.Context ctx = org.mozilla.javascript.Context.enter();
    ctx.setOptimizationLevel(-1);
    ctx.setErrorReporter(new ToolErrorReporter(false));
    ctx.setLanguageVersion(org.mozilla.javascript.Context.VERSION_1_8);
    return ctx;
  }

  /**
   * Creates a initialize the helpers.rhino.js scope.
   *
   * @param ctx A rhino context.
   * @return A handlebars.js scope. Shared between executions.
   */
  private Scriptable helpersEnvScope(final org.mozilla.javascript.Context ctx) {
    Scriptable env = ctx.initStandardObjects();
    env.put("Handlebars_java", env, this);
    ctx.evaluateString(env, HELPERS_ENV, "helpers.rhino.js", 1, null);
    return env;
  }

  /**
   * Load the helper environment.
   *
   * @param location The classpath location.
   * @return The helper environment.
   */
  private static String envSource(final String location) {
    try {
      return Files.read(location);
    } catch (IOException ex) {
      throw new IllegalStateException("Unable to read " + location, ex);
    }
  }

  /**
   * Turn {@link Context} properties into a {@link ScriptableMap}.
   * @param context Handlebars context.
   * @return the ScriptableMap
   */
  private static ScriptableMap toScriptableMap(final Context context) {
    Map<String, Object> hash = new HashMap<String, Object>();
    for (Entry<String, Object> property : context.propertySet()) {
      hash.put(property.getKey(), JsConversion.toJsObject(property.getValue()));
    }
    return new ScriptableMap(hash);
  }

}
