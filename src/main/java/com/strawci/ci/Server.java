package com.strawci.ci;

import com.strawci.ci.cli.Console;
import com.strawci.ci.commands.CommandManager;
import com.strawci.ci.config.DatabaseConfiguration;
import com.strawci.ci.config.ServerConfiguration;
import com.strawci.ci.database.DatabaseProvider;
import com.strawci.ci.database.MongoDBProvider;
import com.strawci.ci.event.debug.DummyEvent;
import com.strawci.ci.networking.ServerHandler;
import com.strawci.ci.plugins.Plugin;
import com.strawci.ci.plugins.PluginManager;
import com.strawci.ci.utils.SystemUtils;

public class Server {

    private DatabaseProvider database;
    private PluginManager pluginManager;
    private ServerHandler serverHandler;
    private CommandManager commandManager;
    private Console console;
    
    private static Server instance;

    public Server () {
        Server.instance = this;
    }

    public static Server getInstance () {
        return Server.instance;
    }

    public DatabaseProvider getDatabase () {
        return this.database;
    }

    public PluginManager getPluginManager () {
        return this.pluginManager;
    }

    public ServerHandler getServerHandler () {
        return this.serverHandler;
    }

    public CommandManager getCommandManager () {
        return this.commandManager;
    }

    public Console getConsole () {
        return this.console;
    }

    public void start () throws Exception {
        System.out.println("Preparing environment.");
        SystemUtils.createDirectories();
        
        System.out.println("Loading configuration.");
        DatabaseConfiguration databaseConfig = new DatabaseConfiguration();
        databaseConfig.load();

        ServerConfiguration serverConfig = new ServerConfiguration();
        serverConfig.load();

        CI.setServer(this);

        System.out.println("Initializing database provider (" + databaseConfig.getDatabaseType());
        this.database = new MongoDBProvider();

        System.out.println("Connecting to Database.");
        this.getDatabase().connect(databaseConfig);
        System.out.println("Database connected.");

        System.out.println("Preparing command manager.");
        this.commandManager = new CommandManager();

        System.out.println("Looking for jars in 'plugins' directory.");
        this.pluginManager = new PluginManager(this);
        this.pluginManager.loadPlugins(SystemUtils.getPluginsDirectory());
        for (Plugin plugin : this.pluginManager.getPlugins()) {
            this.pluginManager.enablePlugin(plugin);
        }

        System.out.println("Starting server listener.");
        this.serverHandler = new ServerHandler(this, serverConfig);
        this.serverHandler.start();
        System.out.println("Server Listening at ws://" + serverConfig.getHost() + ":" + serverConfig.getPort() + "/");

        System.out.println("Done! Server is ready to receive instructions.");

        this.console = new Console(this);
        this.console.start();

        CI.callEvent(new DummyEvent("Hello World", 1234));
    }

    public void stop () {
        System.out.println("Stopping application.");
        this.console.stop();

        System.out.println("Disconnecting pending clients and hooked sockets.");
        this.getServerHandler().stop();

        System.out.println("Disabling plugins.");
        this.pluginManager.clearPlugins();

        System.out.println("Disconnecting from database.");
        this.getDatabase().disconnect();

        System.out.println("Application shutdown.");
    }

    public boolean isPrimaryThread() {
        return false;
    }
}
