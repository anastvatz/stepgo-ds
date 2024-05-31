//package com.example.myandroid;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;


//server sending data to master

public class Worker extends Thread{
    ObjectOutputStream out= null ;
    ObjectInputStream in = null ;
    Socket requestSocket= null ;

    public Worker(){}

    public void run() {

        try{
            // Write IP
            String host = "10.26.49.189";
            //String host = "localhost";
            requestSocket = new Socket(host , 4322);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.getMessage();
        }

        System.out.println("WORKER CONNECTED TO MASTER!!");

        try {
            while (true) {

                String user_file = (String) in.readObject(); // reads the user name and the file name from master(getchunks)
                List<Waypoint> waypoints = (List<Waypoint>) in.readObject(); // reads the waypoints list from master(getchunks)

                System.out.println("WORKER: "+Thread.currentThread().getName()+" received data!");
                WorkerNode worker = new WorkerNode(user_file,waypoints);
                worker.start();

            }
        } catch (IOException ioException) {
            ioException.getMessage();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.getMessage();
            }
        }
    }


    ////////////////////////////WORKERNODE CLASS///////////////////////////

    private class WorkerNode extends Thread {
        List<Waypoint> waypoints;
        public static final double RADIUS_OF_EARTH = 6371; // Earth's radius in kilometers
        double totalDistance;
        double totalTime;
        double totalElevation;
        //double avgSpeed;
        String user_file;

        public WorkerNode(String user_file, List<Waypoint> waypoints){
            this.user_file = user_file;
            this.waypoints = new ArrayList<>(waypoints);
        }

        public void run() {
            try {
                //////STATISTICS OF WAYPOINTS//////////
                totalDistance = 0.0;
                totalTime = 0.0;
                totalElevation = 0.0;

                for (int i = 0; i < waypoints.size() - 1; i++) {
                    Waypoint wp1 = waypoints.get(i);
                    Waypoint wp2 = waypoints.get(i + 1);
                    double distance = calculateDistance(wp1.getLat(), wp1.getLon(), wp2.getLat(), wp2.getLon()); //calculation of distance
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));//converting String to Date
                    Date date1 = format.parse(wp1.getTime());//
                    Date date2 = format.parse(wp2.getTime());//
                    double timeDiff = (date2.getTime() - date1.getTime())/1000; // in seconds

                    double elevationDiff = wp2.getEle() - wp1.getEle();
                    if (elevationDiff>0){
                        totalElevation += elevationDiff; // only the possitive elevation values are counted
                    }
                    totalDistance += distance;
                    totalTime += timeDiff;
                }

                List<Double> statistics = new ArrayList<>(); //adding the 4 totals to a new list
                statistics.add(totalDistance);
                statistics.add(totalTime);
                statistics.add(totalElevation);


                //ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                synchronized (out) {
                    out.writeObject(user_file); //sending to listener the user name and the file name of gpx as string
                    out.writeObject(statistics); //sending the list with the total statistics to the listener
                    out.flush();
                }
            }catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            }catch (IOException ioException) {
                ioException.getMessage();
            }
            catch (Exception e) {
                e.getMessage();
            }
        }

        private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
            double x = Math.toRadians(lon2 - lon1) * Math.cos(Math.toRadians((lat1 + lat2) / 2));
            double y = Math.toRadians(lat2 - lat1);
            double distance = Math.sqrt(x * x + y * y) * RADIUS_OF_EARTH;
            return distance*1000; // in meters
        }

    }

    public static void main(String args[]) {
        new Worker().start();
        new Worker().start();
        new Worker().start();
        new Worker().start();
    }
}