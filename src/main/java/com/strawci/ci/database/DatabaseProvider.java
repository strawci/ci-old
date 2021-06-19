package com.strawci.ci.database;

import java.util.List;

import com.strawci.ci.config.DatabaseConfiguration;
import com.strawci.ci.projects.Project;
import com.strawci.ci.users.User;

public interface DatabaseProvider {
    public void connect (final DatabaseConfiguration config);
    public void disconnect ();

    public Project getProjectByID (final String projectID);
    public List<Project> getAllProjects ();

    public User getUserByID (final String userID);
    public User getUserByName (final String username);
    public List<User> getAllUsers ();
}
