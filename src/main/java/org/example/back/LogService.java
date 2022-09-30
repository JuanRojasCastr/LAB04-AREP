package org.example.back;

import java.util.Date;
import java.util.HashMap;

import static spark.Spark.*;

public class LogService {

    public static void main(String... args){
        port(getPort());
        staticFiles.location("/public");
        get("string", (req,res) -> "Port " + getPort() );
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }

}
