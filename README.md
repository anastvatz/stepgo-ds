<div align="center">
    <img src="/media/Logo.png" width="200">
</div>

### Description <a href="https://emoji.gg/emoji/9531-purple-pin"><img src="https://cdn3.emoji.gg/emojis/9531-purple-pin.png" width="17px" height="17px" alt="Purple_Pin"></a>
<p align="justify">StepGo is an activity tracking mobile app developed in Java using the MapReduce framework. It consists of a mobile frontend application that manages activity tracking as well as a backend system responsible for the analysis of the collected data. Moreover it enables a form of social networking among users through performance leaderboards and charts.
</p>

<p align="justify">Each user has a personal profile, where they can upload their activities. Furthermore, they can view their total statistics such as total distance, total activity time etc. </p>

<div align="center">
    <img src="/media/Splash Screen.png" alt="Login screen" height="350">
    <img src="/media/Route Statistics.png" alt="User Statistics" height="350">
    <img src="/media/Total Statistics.png" alt="Total Statistics"height="350">
</div>


<details>
<summary> <b>Compile</b> </summary>
<p></p>
In folder master-worker respectively: <p>

```javac Master.java```<br>
```javac Worker.java```

> Note: the IPs in User.java and Worker.java have to be changed otherwise the program will not function properly

</p><p></p>
</details>


<details>
<summary> <b>Run</b> </summary> <p></p>
In folder master-worker respectively: <p>
  
```java Master```<br>
```java Worker```
</p>

Then, run the application using a virtual or physical device (Android 8.0 - Oreo OS or more recent).
</details>

<details>
<summary> <b>Map Reduce Framework</b> </summary> <p></p>
The MapReduce framework is a programming model that enables the parallel processing of large volumes of data. It is based on two functions:
- $map(key,value) \rightarrow [(key_2, value_2)]$
- $reduce(key_2,[value_2]) \rightarrow [value_{final}]$


1. Map function: 
    - The input may be all lines of a file (or part of a bigger file) as value, along with its corresponding ID as key $(key, value)$
    - The generated output is another key-value pair $(key_2, value_2)$
    - The map function is such that it can run on multiple inputs on different nodes/machines in parallel. The degree of parallelism can be adjusted


2. Reduce function:
    - Merges all intermediate results associated with the same key and produces the final result(s)
    - Its execution takes place after all map functions are completed

It is based on the MapReduce framework described above. <br>
✔️In this implementation **Master** node is also **Reducer** while **Worker** nodes are **Mappers**. 

- Master runs TCP Server to listen for Workers trying to connect or send intermediate results and it is multithreaded so as to serve many users simultaneously and communicate with Workers in the same time.
- Workers are multithreaded as well, so as to serve many requests comming from Master in parallel. 
- Master communicates with workers to make requests and receive intermediate results via TCP sockets.
</details>

<details>
<summary> <b>Implementation</b> </summary> <p></p>
<p align="justify">
● GPX files
<p></p>
An activity is a sequence of GPS waypoints where each waypoint consists of:</p>

- its coordinates (latitude, longitude)
- its elevation
- the exact time it was recorded

This sequence of waypoints is saved in a specific XML file called GPX like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="user1">
    <wpt lat="52.2423614748556" lon="5.281985213730702">
        <ele>-0.45</ele>
        <time>2023-03-15T10:41:51Z</time>
    </wpt>
    <wpt lat="52.24078476451067" lon="5.294344832871327">
        <ele>-0.06</ele>
        <time>2023-03-15T10:43:59Z</time>
    </wpt>
    ...
</gpx>
```

<p align="justify">A GPX file contains an activity/route and its processing is done in parallel by two or more machines using the Map Reduce framework to accelerate the process.</p>

Step Go Android application enables users to:

- <p align="justify">Select a GPX file, stored in their device and send it to the backend to be processed asynchronously (named as user1, user2 etc given and route1, route2 etc)</p>
- <p align="justify">Receive notification when the processing of the file has finished</p>
- <p align="justify">View GPX results (total distance, mean velocity, total elevation, total time)</p>
- <p align="justify">View their personal statistics compared to other users' statistics (side by side barchart for each performance metric)</p>

<br>
</details>


