<%--
  Author: Ziang Sun (ziangs)
  Last Modified: Apr 8, 2023
  dashboard.jsp is the html showing the Operations analytics (Average Latency Time, most popular user agent,
  and most popular player) and the logs from the MongoDB.
--%>

<%@ page import="org.bson.Document" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>DashBoard</title>
    <style>
        body{
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
        }

        table{
            border-collapse: collapse;
            border: 1px solid;
        }

        table td{
            border: 1px solid;
        }
    </style>
</head>
<body>
    <h1>Operations analytics</h1>
    <div>
        <h2>Average Latency Time (seconds): </h2>
        <ol><%=request.getAttribute("averageLatency")%> seconds</ol>
    </div>
    <div>
        <h2>Most popular user agent: </h2>
        <ol>
            <%String agent = request.getAttribute("mostPopularAgent").toString();%>
            <li><%= agent %> </li>
        </ol>
    </div>
    <div>
        <h2>Most popular player: </h2>
        <ol>
            <%String player = request.getAttribute("mostPopularPlayer").toString();%>
            <li><%= player %> </li>
        </ol>
    </div>


    <br>
    <h1>Logs</h1>
    <table>
        <thead>
            <tr>
                <th>User Agent</th>
                <th>Request TimeStamp</th>
                <th>Request Parameters</th>
                <th>Get ID TimeStamp</th>
                <th>Player Statistics</th>
                <th>Response Timestamp</th>
            </tr>
        </thead>
        <tbody>
            <% ArrayList<Document> logs = (ArrayList<Document>) request.getAttribute("logs"); %>
            <% for (Document log: logs){ %>
            <tr>
                <td><%=log.getString("userAgent")%></td>
                <td><%=log.getDate("requestTimeStamp")%></td>
                <td><%=log.getString("requestParameter")%></td>
                <td><%=log.getDate("getIDTimeStamp")%></td>
                <td><%=log.getString("playerStatistics")%></td>
                <td><%=log.getDate("responseTimeStamp")%></td>
            </tr>
        <%}%>
        </tbody>
    </table>
<br/>
</body>
</html>