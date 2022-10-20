package org.example.back;

import org.example.back.connection.MongoDBConnection;

import java.util.List;

import static spark.Spark.*;

public class LogService {

    public static void main(String... args){
        MongoDBConnection mongoConnection = new MongoDBConnection();

        port(getPort());

        // Allow CORS
        options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                    }

                    return "OK";
                });

        get("query", (req,res) -> {
            mongoConnection.createConnection();
            List<String> messages = mongoConnection.getAllItems();
            mongoConnection.closeConnection();
            return messages;
        } );

        post("query", (req,res) -> {
            mongoConnection.createConnection();
            if(req.body()!=null){
                mongoConnection.addItem(req.body());
            }
            List<String> messages = mongoConnection.getAllItems();
            mongoConnection.closeConnection();
            return messages;
        });
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }

}
