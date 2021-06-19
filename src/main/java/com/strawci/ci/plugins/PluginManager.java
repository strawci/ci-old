package com.strawci.ci.plugins;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.strawci.ci.CI;
import com.strawci.ci.Server;
import com.strawci.ci.event.Event;
import com.strawci.ci.event.EventExecutor;
import com.strawci.ci.event.EventPriority;
import com.strawci.ci.event.HandlerList;
import com.strawci.ci.event.Listener;
import com.strawci.ci.plugins.errors.IllegalPluginAccessException;
import com.strawci.ci.plugins.errors.InvalidDescriptorException;
import com.strawci.ci.plugins.errors.InvalidPluginException;

public class PluginManager {

    private final Server server;
    private final PluginLoader loader;
    private final List<Plugin> plugins;

    public PluginManager(final Server server) {
        this.server = server;
        this.loader = new PluginLoader();
        this.plugins = new ArrayList<Plugin>();
    }

    private void fireEvent(Event event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (RegisteredListener registration : listeners) {
            if (!registration.getPlugin().isEnabled()) {
                continue;
            }

            try {
                registration.callEvent(event);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
            }
        }
    }

    private HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    public Plugin getPlugin(String name) {
        for (final Plugin plugin : this.plugins) {
            if (plugin.getName().equalsIgnoreCase(name)) {
                return plugin;
            }
        }

        return null;
    }

    public Plugin[] getPlugins() {
        return this.plugins.toArray(new Plugin[0]);
    }

    public boolean isPluginEnabled(String name) {
        final Plugin plugin = getPlugin(name);
        return isPluginEnabled(plugin);
    }

    public boolean isPluginEnabled(Plugin plugin) {
        if ((plugin != null) && (plugins.contains(plugin))) {
            return plugin.isEnabled();
        } else {
            return false;
        }
    }

    public Plugin loadPlugin(File file) throws InvalidPluginException, InvalidDescriptorException, IOException {
        Plugin result = this.loader.loadPlugin(file);

        if (result != null) {
            this.plugins.add(result);
        }

        return result;
    }

    public Plugin[] loadPlugins(File directory) throws IOException {
        List<Plugin> result = new ArrayList<Plugin>();

        for (final File file : directory.listFiles()) {
            try {
                result.add(this.loadPlugin(file));
            } catch (final InvalidDescriptorException e) {
                continue;
            } catch (InvalidPluginException e) {
                e.printStackTrace();
            }
        }

        return result.toArray(new Plugin[result.size()]);
    }

    public void disablePlugins() {
        for (Plugin plugin : this.getPlugins()) {
            this.disablePlugin(plugin);
        }
    }

    public void clearPlugins() {
        synchronized (this) {
            disablePlugins();
            plugins.clear();
            HandlerList.unregisterAll();
        }
    }

    public void callEvent(Event event) throws IllegalStateException {
        if (event.isAsynchronous()) {
            if (Thread.holdsLock(this)) {
                throw new IllegalStateException(
                        event.getEventName() + " cannot be triggered asynchronously from inside synchronized code.");
            }

            if (this.server.isPrimaryThread()) {
                throw new IllegalStateException(
                        event.getEventName() + " cannot be triggered asynchronously from primary server thread.");
            }

            this.fireEvent(event);
        } else {
            synchronized (this) {
                this.fireEvent(event);
            }
        }
    }

    public void registerEvents(Listener listener, Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        }

        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : plugin.getPluginLoader()
                .createRegisteredListeners(listener, plugin).entrySet()) {
            this.getEventListeners(this.getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority,
            EventExecutor executor, Plugin plugin) {
        this.registerEvent(event, listener, priority, executor, plugin, false);
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority,
            EventExecutor executor, Plugin plugin, boolean ignoreCancelled) {

        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
        }

        this.getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
    }

    public void enablePlugin(Plugin plugin) {
        if (!plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().enablePlugin(plugin);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            HandlerList.bakeAll();
        }

    }

    public void disablePlugin(Plugin plugin) {
        if (plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            
            try {
                HandlerList.unregisterAll(plugin);
                CI.getCommandManager().unregisterByPlugin(plugin);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
