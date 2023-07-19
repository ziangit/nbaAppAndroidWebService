# nbaAppAndroidWebService
This is the web service part of my 95702 class project, it serves as the backend for the Android NBA application. 

For the Android Application: 
//todo

For the GitHub CodeSpace deployment:
https://ziangit-ubiquitous-space-fishstick-9r5x6r4v9g9cxqvr.github.dev/

The distributed NBA application will run on an Android device. 
The user can search for NBA player’s seasonal statistics by entering the player’s first name, last name, and the season. 
After the user enters the data and clicks the “submit” button, the Android app makes an HTTP request to the web service deployed on GitHub, 
and the web service calls a third-party API, performs the calculation, and returns the seasonal statistics of the player to the Android app.
Besides the Android app, the distribution application also has a browser-based dashboard which records three interesting statistics 
(average latency, most popular player, most popular user agent) and logs. This is achieved by a NoSQL database MongoDB. 
The webservice records the logs and useful statistics, stores them in the database, and displays the data on the dashboard.

For the 

# Diagram
The following is a diagram of the components for this part of the NBA application:

![Diagram-Full](https://github.com/ziangit/nbaAppAndroidWebService/assets/110576506/6be960f7-2a7e-4cd8-b2e9-2011a4c5f4e1)

# Operational Analytics

![Operations analytics](https://github.com/ziangit/nbaAppAndroidWebService/assets/110576506/3e8c5fad-57bc-47df-a5f3-ac969f28deb9)

# NBA Statistics

![Stats](https://github.com/ziangit/nbaAppAndroidWebService/assets/110576506/55daa991-c773-43fa-a5a0-6bc00d2f46ca)
