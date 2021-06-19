package com.strawci.ci.projects;

public class Project {

    private final String id;
    private final String name;
    private final String description;

    public Project (final String id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getID () {
        return this.id;
    }

    public String getName () {
        return this.name;
    }

    public String getDescription () {
        return this.description;
    }
}
