package com.strawci.ci.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.strawci.ci.event.Event;
import com.strawci.ci.event.EventException;
import com.strawci.ci.event.EventExecutor;
import com.strawci.ci.event.EventHandler;
import com.strawci.ci.event.Listener;
import com.strawci.ci.plugins.errors.InvalidDescriptorException;
import com.strawci.ci.plugins.errors.InvalidPluginException;

import org.yaml.snakeyaml.error.YAMLException;

public class PluginLoader {

    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private final Map<String, PluginClassLoader> loaders = new HashMap<String, PluginClassLoader>();

    public Plugin loadPlugin (final File file) throws InvalidPluginException, IOException {
        if (!file.exists()) {
            throw new InvalidPluginException(new FileNotFoundException(file.getPath() + " doest not exist."));
        }

        final PluginDescriptor descriptor;
        try {
            descriptor = this.loadPluginDescriptor(file);
        } catch (Exception e) {
            throw new InvalidPluginException(e);
        }

        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, descriptor.getName());
        
        final PluginClassLoader loader;
        try {
            loader = new PluginClassLoader(this, getClass().getClassLoader(), descriptor, dataFolder, file);
        } catch (InvalidPluginException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvalidPluginException(e);
        }

        this.loaders.put(descriptor.getName(), loader);
        return loader.getPlugin();
    }

    public PluginDescriptor loadPluginDescriptor (final File file) throws InvalidDescriptorException {
        JarFile jar = null;
        InputStream stream = null;

        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("straw-plugin.yml");

            if (entry == null) {
                throw new InvalidDescriptorException(new FileNotFoundException("Jar does not contain straw-plugin.yml"));
            }

            stream = jar.getInputStream(entry);
            return new PluginDescriptor(stream);
        } catch (final IOException e) {
            throw new InvalidDescriptorException(e);
        } catch (final YAMLException e) {
            throw new InvalidDescriptorException(e);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {}
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {}
            }
        }
    }

    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<Class<? extends Event>, Set<RegisteredListener>>();
        Set<Method> methods;
        
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            methods = new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
            for (Method method : publicMethods) {
                methods.add(method);
            }
            for (Method method : listener.getClass().getDeclaredMethods()) {
                methods.add(method);
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            return ret;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;

            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                System.out.println(plugin.getName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\n in " + listener.getClass());
                continue;
            }

            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredListener> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<RegisteredListener>();
                ret.put(eventClass, eventSet);
            }

            EventExecutor executor = new EventExecutor(){
                public void execute (Listener listener, Event event) throws EventException {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }

                        method.invoke(listener, event);
                    } catch (InvocationTargetException e) {
                        throw new EventException(e.getCause());
                    } catch (Throwable t) {
                        throw new EventException(t);
                    }
                }
            };

            eventSet.add(new RegisteredListener(listener, executor, eh.priority(), plugin, eh.ignoreCancelled()));
        }

        return ret;
    }

    public void enablePlugin(Plugin plugin) {
        if (!plugin.isEnabled()) {
            System.out.println("Enabling plugin " + plugin.getName());
            try {
                plugin.setEnabled(true);
            } catch (Throwable e) {
                System.out.println("Error ocurred while enabling " + plugin.getName());
                e.printStackTrace();
            }
        }
    }

    public void disablePlugin(Plugin plugin) {
        if (plugin.isEnabled()) {
            System.out.println("Disabling plugin " + plugin.getName());

            try {
                plugin.setEnabled(false);
            } catch (Throwable e) {
                System.out.println("Error ocurred while disabling " + plugin.getName());
                e.printStackTrace();
            }
        }
    }

    public Class<?> getClassByName(String name) {
        Class<?> result = this.classes.get(name);

        if (result == null) {
            for (final String pluginName : loaders.keySet()) {
                final PluginClassLoader loader = loaders.get(pluginName);
                try {
                    result = loader.findClass(name, false);
                } catch (final ClassNotFoundException e) {}
            }
        }

        return result;
    }

    public void setClass(String name, Class<?> result) {
        if (!this.classes.containsKey(name)) {
            this.classes.put(name, result);
        }
    }

    public void removeClass (final String name) {
        this.classes.remove(name);
    }
}