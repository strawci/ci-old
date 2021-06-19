package com.strawci.ci.database;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.strawci.ci.config.DatabaseConfiguration;
import com.strawci.ci.projects.Project;
import com.strawci.ci.users.User;
import com.strawci.ci.utils.DatabaseUtils;

import org.bson.Document;

public class MongoDBProvider implements DatabaseProvider {

    private MongoClient mongoClient;
    private MongoDatabase database;

    @Override
    public void connect (final DatabaseConfiguration config) {
        ConnectionString connString = new ConnectionString(config.toURI());
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .retryWrites(true)
            .build();

        this.mongoClient = MongoClients.create(settings);
        this.database = this.mongoClient.getDatabase(config.getDatabase());
    }

    @Override
    public void disconnect () {
        this.mongoClient.close();
    }

    @Override
    public Project getProjectByID(String projectID) {
        MongoCollection<Document> collection = database.getCollection("projects");
        Document doc = collection.find(new Document("_id", projectID)).first();
        return DatabaseUtils.projectFromDocument(doc);
    }

    @Override
    public List<Project> getAllProjects() {
        MongoCollection<Document> collection = database.getCollection("projects");
        List<Project> projects = new ArrayList<>();

        for (Document doc : collection.find()) {
            projects.add(DatabaseUtils.projectFromDocument(doc));
        }

        return projects;
    }

    @Override
    public User getUserByID(String userID) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document doc = collection.find(new Document("_id", userID)).first();
        return DatabaseUtils.userFromDocument(doc);
    }

    @Override
    public User getUserByName(String username) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document doc = collection.find(new Document("username", username)).first();
        return DatabaseUtils.userFromDocument(doc);
    }

    @Override
    public List<User> getAllUsers() {
        MongoCollection<Document> collection = database.getCollection("users");
        List<User> users = new ArrayList<>();

        for (Document doc : collection.find()) {
            users.add(DatabaseUtils.userFromDocument(doc));
        }

        return users;
    }
}
