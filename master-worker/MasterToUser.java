//package com.example.myandroid;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Object;


public class MasterToUser extends Thread{
    static HashMap<String,List<Waypoint>> user_wpts = new HashMap<>();
    static HashMap<String,Socket> user_socket = new HashMap<>();
    /* Define the socket that receives requests */
    ServerSocket s;
    /* Define the socket that is used to handle the connection */

    public MasterToUser(){}

    public void run() {

        try {

            /* Create Server Socket */
            s = new ServerSocket(4321, 10);
            while (true) {
                /* Accept the connection */
                Socket providerSocket = s.accept();
                System.out.println("User added!!");
                UserFiles user = new UserFiles(providerSocket);
                user.start(); //create thread to process the files
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                s.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public List<Waypoint> processFile(String gpx){
        Pattern pattern;
        Matcher matcher;
        List<Waypoint> waypoints = new ArrayList<>();

        pattern = Pattern.compile("<wpt lat=\"([-\\d.]+)\" lon=\"([-\\d.]+)\">\\s*" +
                "<ele>([-\\d.]+)</ele>\\s*" +
                "<time>(.*?)</time>\\s*" +
                "</wpt>");
        matcher = pattern.matcher(gpx);

        while (matcher.find()) {
            double lat = Double.parseDouble(matcher.group(1)); // taking the lat value from the pattern
            double lon = Double.parseDouble(matcher.group(2)); // taking the lon value from the pattern
            double ele = Double.parseDouble(matcher.group(3)); // taking the ele value from the pattern
            String time = matcher.group(4); // taking the time value from the pattern
            Waypoint waypoint = new Waypoint(lat, lon, ele, time); //creating the waypoint
            waypoints.add(waypoint);
        }

        return waypoints;
    }

////////////////////////////UserFiles CLASS///////////////////////////

    private class UserFiles extends Thread{
        private Socket providerSocket;
        StringBuilder gpxData;

        public UserFiles(Socket providerSocket) {
            this.providerSocket = providerSocket;
        }


        public void run() {
            InputStreamReader in = null;
            PrintWriter out = null;

            try {

                out = new PrintWriter(providerSocket.getOutputStream());
                in = new InputStreamReader(providerSocket.getInputStream());
                BufferedReader reader = new BufferedReader(in);
                String line;
                gpxData = new StringBuilder();
                user_wpts = new HashMap<>();
                String user = null;

                // Receive files from User
                String file = reader.readLine();

                while ((line = reader.readLine()) != null) {
                    Pattern pattern_user = Pattern.compile("creator=\"(.*?)\">");
                    Matcher matcher_user = pattern_user.matcher(line);
                    if (matcher_user.find()){user = matcher_user.group(1);}
                    gpxData.append(line).append("\n");
                    if (line.trim().equals("</gpx>")) {
                        //call function for the gpx data per user before deleting them and moving on to the next user
                        synchronized (user_wpts) {
                            user_wpts.put(user + " " + file,processFile(gpxData.toString()));
                            user_socket.put(user,providerSocket);
                        }
                        file = reader.readLine();
                        gpxData.setLength(0);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}