package com.strawci.ci.utils;

import com.strawci.ci.projects.Project;
import com.strawci.ci.users.User;

import org.bson.Document;

public class DatabaseUtils {

    public static User userFromDocument (final Document doc) {
        if (doc == null) {
            return null;
        }

        String id = doc.getString("_id");
        String username = doc.getString("username");
        String password = doc.getString("password");

        return new User(id, username, password);
    }

    public static Project projectFromDocument (final Document doc) {
        if (doc == null) {
            return null;
        }
        
        String id = doc.getString("_id");
        String name = doc.getString("name");
        String description = doc.getString("description");

        return new Project(id, name, description);
    }
}