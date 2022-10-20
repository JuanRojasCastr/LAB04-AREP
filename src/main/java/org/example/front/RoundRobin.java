package org.example.front;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import static spark.Spark.*;

public class RoundRobin {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String BASE_URL = "http://ec2-3-88-11-222.compute-1.amazonaws.com:3500";
    private static int roundRobinCount = 0;

    public static void main(String... args){
        HashMap<String, Date> memory = new HashMap<>();
        port(getPort());
        staticFiles.location("/public");

        get("string", (req,res) -> getResp());

        get("favicon.ico", (req,res) -> "");

        post("string", (req,res) -> postResp(req.body()));
    }


    public static String postResp(String str) throws IOException {
        String url = BASE_URL + getRoundRobinCount() + "/query";
        roundRobinCount ++;
        System.out.println("post to " + url);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/plain");
        con.setRequestProperty("Accept", "text/plain");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = str.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "POST request not worked";
        }
    }

    public static String getResp() throws IOException {
        URL obj = new URL(BASE_URL + getRoundRobinCount() + "/query");
        roundRobinCount ++;
        System.out.println(obj.toString());
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

            return response.toString();
        } else {
            return "GET request not worked";
        }
    }

    private static int getRoundRobinCount() {
        if (roundRobinCount > 2) roundRobinCount = 0;
        return roundRobinCount;
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4566;
    }
}
