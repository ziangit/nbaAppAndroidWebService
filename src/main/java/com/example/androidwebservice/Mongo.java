/**
 * Author: Ziang Sun (ziangs)
 * Last Modified: Apr 8, 2023
 *
 * Mongo class is used to store the  logging information in MongoDB,
 * and then show it in the dashboard webpage.
 *
 */
package com.example.androidwebservice;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class Mongo {
    private static MongoDatabase database;
    private static MongoClient mongoClient;
    private static MongoCollection<Document> collection;
    private static ConnectionString connectionString;
    private static MongoClientSettings settings;

    /**
     * Constructor that connects to the MongoDB
     */
    public Mongo(){
        connectMongoDB();
    }

    /**
     * Used sample code from MongoDB website:
     * https://cloud.mongodb.com/v2/6418ba8620ee9c575f9b2376#/overview
     * In this project, I will just use the collection "log" to store the data
     */
    public static void connectMongoDB(){

        // Sample code cited from MongoDB code example
        connectionString = new ConnectionString("{mongoDBURL}");
        settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("test");
        collection= database.getCollection("log");
    }

    /**
     * Insert Organized Document into the MongoDB collection
     * @param doc
     * @postcondition
     * Document is inserted
     */

    public static void insertDoc(Document doc){
        collection = database.getCollection("log");
        collection.insertOne(doc);
    }


    /**
     * Return the ArrayList of the documents stored in collection "log"
     * @return
     * ArrayList of documents needed
     */
    public static ArrayList<Document> fetchLog(){
        ArrayList<Document> list = new ArrayList<>();
        for (Document doc: collection.find()){
            list.add(doc);
        }
        return list;
    }
}
