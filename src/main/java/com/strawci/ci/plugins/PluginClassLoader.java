package com.strawci.ci.plugins;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.strawci.ci.plugins.errors.InvalidPluginException;

public class PluginClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> classes;
    private final PluginLoader loader;
    private final Plugin plugin;

    public PluginClassLoader (final PluginLoader loader, final ClassLoader parent, final PluginDescriptor descriptor, final File dataFolder, final File file) throws IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InvalidPluginException, MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);

        this.classes = new HashMap<String, Class<?>>();
        this.loader = loader;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(descriptor.getMain(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new InvalidPluginException("Cannot find main class `" + descriptor.getMain() + "'", ex);
            }

            Class<? extends Plugin> pluginClass;
            try {
                pluginClass = jarClass.asSubclass(Plugin.class);
            } catch (ClassCastException ex) {
                throw new InvalidPluginException("main class `" + descriptor.getMain() + "' does not extend JavaPlugin", ex);
            }

            this.plugin = pluginClass.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException ex) {
            throw new InvalidPluginException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidPluginException("Abnormal plugin type", ex);
        }

        plugin.init(loader, descriptor, dataFolder);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        return this.findClass(name, true);
    }

    protected Class<?> findClass(final String name, final boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = this.classes.get(name);

        if (result == null && checkGlobal) {
            result = this.loader.getClassByName(name);
        }

        if (result == null) {
            result = super.findClass(name);
        }

        if (result != null) {
            this.loader.setClass(name, result);
        }

        if (this.classes.get(name) == null && result != null) {
            this.classes.put(name, result);
        }

        return result;
    }

    public final Set<String> getClasses() {
        return classes.keySet();
    }

    public final Plugin getPlugin() {
        return this.plugin;
    }
}
