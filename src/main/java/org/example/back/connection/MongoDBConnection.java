package org.example.back.connection;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class MongoDBConnection {
    private String url = "ec2-3-82-206-61.compute-1.amazonaws.com";
    private int port = 27017;
    private MongoClient mongoClient = null;
    private MongoDatabase mongoDatabase = null;
    private MongoCollection<Document> mongoCollection;


    public void createConnection() {
        try {
            this.mongoClient = new MongoClient(this.url);
            this.mongoDatabase = this.mongoClient.getDatabase("items");
            this.mongoCollection = this.mongoDatabase.getCollection("myItems");
        } catch (MongoException ex){
            System.out.println(ex.toString());
        }
    }

    public void closeConnection() {
        this.mongoClient.close();
    }

    public ArrayList<String> addItem(String item){
        ArrayList<String> documents = new ArrayList<>();

        Document myDocument = new Document();
        myDocument.put("text", item);
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateObj.format(formatter);

        myDocument.put("date", date);

        this.mongoCollection.insertOne(myDocument);
        documents.add(myDocument.toJson());

        return documents;
    }

    public List<String> getAllItems() {
        ArrayList<String> messages = new ArrayList<>();

        FindIterable<Document> result = this.mongoCollection.find();

        result.forEach((Consumer<? super Document>) document -> messages.add(document.toJson()));

        return messages.subList(Math.max(messages.size() - 10, 0), messages.size());
    }




}
