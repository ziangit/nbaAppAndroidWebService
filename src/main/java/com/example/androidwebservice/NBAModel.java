/**
 * Author: Ziang Sun (ziangs)
 * Last Modified: Apr 8, 2023
 *
 * NBAModel provides searching methods for the NBA player's personal statistics
 * based on the input of player first name, player last name, and season.
 * Since the app uses a different measuring approach to do the statistics (all >0 min games count),
 * it might be a little off from the official NBA website.
 *
 * This app fetches the third party API from:
 * https://rapidapi.com/api-sports/api/api-nba
 * It is free, and the API key is included in the methods.
 *
 */
package com.example.androidwebservice;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import org.bson.Document;

import java.sql.Timestamp;
import java.util.*;

public class NBAModel {

    // MongoDB to store data for logging
    private static Mongo mg;
    // Document to be stored
    private static Document logDoc;

    /**
     * getPlayerStats is called from the Servlet. It calls getPlayerIDByName to get the player ID,
     * and then calls playerStatsByID to get and return the JSON of the player statistics.
     * @param firstName
     * @param lastName
     * @param season
     * @return
     * JSON statistics
     */
    public static String getPlayerStats(String firstName, String lastName, String season, String userAgent){
        // Connect to mongoDB
        mg = new Mongo();
        mg.connectMongoDB();
        logDoc = new Document();
        // logDoc appends the needed data and later stores in the MongoDB
        logDoc.append("userAgent", userAgent) // User Agent is important for user information statistics
                .append("requestTimeStamp", new Timestamp(System.currentTimeMillis())) // Request time
                .append("requestParameter", firstName + " " + lastName + " " + season); // See user input parameter

        // Get playerID
        int playerID = getPlayerIDByName(firstName, lastName);

        logDoc.append("getIDTimeStamp", new Timestamp(System.currentTimeMillis())); // Time to get ID for further operation
        // JSON format stats
        String stats = playerStatsByID(String.valueOf(playerID), season);

        System.out.println(stats);
        Gson gson = new Gson();
        // Use Gson to break down
        Player player = gson.fromJson(stats, Player.class);
        System.out.println(player);
        String TotalGames = player.getTotalGames();
        String AveragePoints = player.getAveragePoints();
        String AverageRebounds = player.getAverageRebounds();
        String AverageAssists = player.getAverageAssists();
        logDoc.append("playerStatistics", TotalGames + " " + AveragePoints + " " +
                " " + AverageRebounds + " " + AverageAssists); // Response player statistics
        logDoc.append("responseTimeStamp", new Timestamp(System.currentTimeMillis())); // Response time

        // Insert doc to the MongoDB
        mg.insertDoc(logDoc);

        return stats;
    }

    /**
     * getPlayerIDByName gets the ID of the player by Name.
     * It first fetches the response from the third party API,
     * and check if the first name and last name correspond.
     * If the answer is yes, return the valid playerID, else return -1.
     * The invalid server-side input is handled here:
     * Since id -1 does not refer to any player, the games played by that id:-1 player
     * along with other statistics will be 0.
     *
     * Used some sample codes from the NBA API website to make the HTTP request, source:
     * https://rapidapi.com/api-sports/api/api-nba
     *
     * @param firstName
     * @param lastName
     * @return ID
     */
    public static int getPlayerIDByName(String firstName, String lastName){
        // Fetch data
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-nba-v1.p.rapidapi.com/players?name=" + lastName))
                .header("X-RapidAPI-Key", "{key}")
                .header("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Since the returned JSON cannot be broken down by Gson, here I used ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // Default not found -1, if found, set playerID to new ID
        int playerID = -1;
        try{
            Map<String, Object> jsonMap = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> playerDataList = (List<Map<String, Object>>) jsonMap.get("response");

            // If first name and last name correspond to user input, return valid id, else return -1
            for (Map<String, Object> playerData: playerDataList){
                if (firstName.equalsIgnoreCase((String) playerData.get("firstname"))&&
                        lastName.equalsIgnoreCase((String) playerData.get("lastname"))){
                    playerID = (int) playerData.get("id");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playerID;

    }

    /**
     * playerStatsByID return the JSON representation of the average statistics
     * of the player given the id and season.
     * the method first fetches the data from the API, then calculates the total games,
     * average points, average rebounds, and average assists of the player.
     * Finally, it returns the JSON of the player statistics.
     *
     * Used some sample codes from the NBA API website to make the HTTP request, source:
     * https://rapidapi.com/api-sports/api/api-nba
     *
     * @param id
     * @param season
     * @return
     * JSON Statistics of the player
     */
    public static String playerStatsByID(String id, String season) {
        // Fetch data
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-nba-v1.p.rapidapi.com/players/statistics?id=" + id + "&season=" + season))
                .header("X-RapidAPI-Key", "{key}")
                .header("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        // Use objectMapper to handle the JSON data
        String JSONResponse = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> gameStats = (List<Map<String, Object>>) jsonMap.get("response");

            double totalPoints = 0;
            double totalReb = 0;
            double totalAssists = 0;
            int games = 0;

            // Record the games, points, rebounds, assists
            for (Map<String, Object> gameStat : gameStats) {
                String min = (String) gameStat.get("min");
                if (min != null && !min.equals("0:0")) {
                    Integer points = (Integer) gameStat.get("points");
                    Integer totReb = (Integer) gameStat.get("totReb");
                    Integer assists = (Integer) gameStat.get("assists");

                    if (points != null) {
                        totalPoints += points;
                    }
                    if (totReb != null) {
                        totalReb += totReb;
                    }
                    if (assists != null) {
                        totalAssists += assists;
                    }

                    games++;
                }
            }

            // Calculate average

            double averagePoints = totalPoints / games;
            double averageRebounds = totalReb / games;
            double averageAssists = totalAssists / games;

            String AVP = String.format("%.2f", averagePoints);
            String AVR = String.format("%.2f", averageRebounds);
            String AVA = String.format("%.2f", averageAssists);

            // Use ObjectMapper to format JSON
            ObjectMapper objectMapper2 = new ObjectMapper();
            ObjectNode node = objectMapper2.createObjectNode();

            node.put("TotalGames", games);
            node.put("AveragePoints", AVP);
            node.put("AverageRebounds", AVR);
            node.put("AverageAssists", AVA);

            // Convert to JSON
            JSONResponse = objectMapper2.writeValueAsString(node);


        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return JSON
        return JSONResponse;
    }

    /**
     * Get the most popular user agent
     * @param logs
     * @return
     * most popular user agent
     */
    public static String getMostUsedUserAgent(ArrayList<Document> logs){
        HashMap<String, Integer> map = new HashMap<>();
        String mostUsed = "";
        int max = 0;
        for (Document doc: logs){
            String agent = doc.getString("userAgent");
            map.put(agent, map.getOrDefault(agent, 0) + 1);
        }

        for (String agent: map.keySet()){
            if (map.get(agent) > max){
                max = map.get(agent);
                mostUsed = agent;
            }
        }
        return mostUsed;
    }

    /**
     * Get the most popular player
     * @param logs
     * @return
     * most popular player
     */
    public static String getMostPopularPlayer(ArrayList<Document> logs){
        HashMap<String, Integer> map = new HashMap<>();
        String mostUsed = "";
        int max = 0;
        for (Document doc: logs){
            String[] request = doc.getString("requestParameter").split(" ");
            String player = "";
            if (request.length >= 2){
                player = request[0] + " " + request[1];
            }

            map.put(player, map.getOrDefault(player, 0) + 1);
        }

        for (String player: map.keySet()){
            if (map.get(player) > max){
                max = map.get(player);
                mostUsed = player;
            }
        }
        return mostUsed;
    }

    /**
     * Calculate the average latency, which is the average time interval between request and response
     * @param logs
     * @return
     * Average latency
     */

    public static double getAverageLatency(ArrayList<Document> logs){
        double totalLatency = 0.0;
        int count = logs.size();

        for (Document doc: logs){
            Date requestDate = doc.getDate("requestTimeStamp");
            Date responseDate = doc.getDate("responseTimeStamp");

            long latency = responseDate.getTime() - requestDate.getTime();
            totalLatency += latency;
        }

        return (totalLatency / count) / 1000.0;

    }
}