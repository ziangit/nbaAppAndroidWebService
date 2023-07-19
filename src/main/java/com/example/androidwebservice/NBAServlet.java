/**
 * Author: Ziang Sun (ziangs)
 * Last Modified: April 8, 2023
 *
 * NBAServlet is the servlet acting as the controller which takes in the
 * HTTP request from the Android Application. It then calls NBAModel to fetch the data
 * from the third party API and provides useful json data as the response to the
 * Android Application
 *
 *
 */
package com.example.androidwebservice;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;


@WebServlet(name = "NBAServlet",
        urlPatterns = {"/getNBAStats"})
public class NBAServlet extends HttpServlet {

    // Model
    NBAModel nbam = null;


    @Override
    public void init() {
        nbam = new NBAModel();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Get request parameters
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String season = request.getParameter("season");
        String dashboard = request.getParameter("dashboard");
        String userAgent = request.getHeader("User-Agent");

        String nextView;

        // If dashboard parameter is null or other than 0, go to the dashboard html page
        if (dashboard == null || !dashboard.equals("0")){
            // fetch data from the MongoDB and set the attributes for the dashboard
            Mongo mongo = new Mongo();
            ArrayList<Document> logs = mongo.fetchLog();
            request.setAttribute("logs", logs);
            String mostPopularAgent = nbam.getMostUsedUserAgent(logs);
            request.setAttribute("mostPopularAgent", mostPopularAgent);
            String mostPopularPlayer = nbam.getMostPopularPlayer(logs);
            request.setAttribute("mostPopularPlayer", mostPopularPlayer);
            double averageLatency = nbam.getAverageLatency(logs);
            request.setAttribute("averageLatency", averageLatency);
            nextView = "dashboard.jsp";
        }
        // else, get the JSON from the third party API
        else {
            String jsonResponse = nbam.getPlayerStats(firstName, lastName, season, userAgent);
            response.setContentType("application/json");
            request.setAttribute("JSON", jsonResponse);

            nextView = "index.jsp";
        }


        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }


}

