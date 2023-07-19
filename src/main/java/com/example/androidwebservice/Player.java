/**
 * Author: Ziang Sun (ziangs)
 * Last Modified: Apr 8, 2023
 *
 * Player class is used to store the player statistics after getting the data from
 * the Third Party API and finishing the calculation of data.
 * After the program gets the JSON from the third party API, the stats are converted to the needed data
 * in JSON format. The player class is used to break down the JSON.
 *
 */
package com.example.androidwebservice;

public class Player {
    private String TotalGames;
    private String AveragePoints;
    private String AverageRebounds;
    private String AverageAssists;

    public String getTotalGames() {
        return TotalGames;
    }

    public String getAveragePoints() {
        return AveragePoints;
    }

    public String getAverageRebounds() {
        return AverageRebounds;
    }

    public String getAverageAssists() {
        return AverageAssists;
    }

    public void setTotalGames(String totalGames) {
        TotalGames = totalGames;
    }

    public void setAveragePoints(String averagePoints) {
        AveragePoints = averagePoints;
    }

    public void setAverageRebounds(String averageRebounds) {
        AverageRebounds = averageRebounds;
    }

    public void setAverageAssists(String averageAssists) {
        AverageAssists = averageAssists;
    }

    @Override
    public String toString() {
        return "Player{" +
                "TotalGames='" + TotalGames + '\'' +
                ", AveragePoints='" + AveragePoints + '\'' +
                ", AverageRebounds='" + AverageRebounds + '\'' +
                ", AverageAssists='" + AverageAssists + '\'' +
                '}';
    }
}
