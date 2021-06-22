package com.strawci.ci.commands;

import java.io.File;

public class ExecutionContext {
    
    private final ExecutionType type;
    private final File directory;

    public ExecutionContext () {
        this.type = ExecutionType.CONSOLE;
        this.directory = null;
    }

    public ExecutionContext (final ExecutionType type, final File directory) {
        this.type = type;
        this.directory = directory;
    }

    public final File getDirectory () {
        return this.directory;
    }

    public final ExecutionType getType () {
        return this.type;
    }

    public final boolean isType (final ExecutionType comparation) {
        return comparation.equals(this.type);
    }

    public final static ExecutionContext createDefault () {
        return new ExecutionContext();
    }
}
