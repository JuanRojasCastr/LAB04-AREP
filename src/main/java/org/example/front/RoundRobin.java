package org.example.front;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import static spark.Spark.*;

public class RoundRobin {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "localhost:35000";

    public static void main(String... args){
        HashMap<String, Date> memory = new HashMap<>();
        port(getPort());
        staticFiles.location("/public");
        get("string", (req,res) -> {
            String value = req.queryParams("value");


            URL obj = new URL(GET_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return value + response;
            } else {
                return "GET request not worked";
            }
        });
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
