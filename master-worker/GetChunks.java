//package com.example.myandroid;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

public class GetChunks extends Thread {

    int currentIndex; // the element of the list of wpts that is processed
    static HashMap<String,Integer> file_chunks = new HashMap<>(); //contains the user and file name, the size of the chunks that every file has

    public GetChunks() { currentIndex = 0;}

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (MasterToUser.user_wpts.size() > 0) { //if the user class has sent a file
                List<String> temp = new ArrayList<>(MasterToUser.user_wpts.keySet()); //temp includes all the keys from MasterToUser.user_wpts
                while(temp.size()>0){
                    String key = temp.remove(0); //remove first element, get a string user_file
                    List<List<Waypoint>> chunks = getChunks(MasterToUser.user_wpts.get(key)); //for every user_file, get the chunks for the value, the value is the gpx in a List<Waypoint> form
                    String file = key.split(" ")[1];

                    synchronized (file_chunks) {
                        file_chunks.put(file,chunks.size()); //insert the name of file and the number of chunks that the file is divided in the hashmap file_chunks
                    }

                    for (List<Waypoint> chunk : chunks) { //assign each chunk to another worker using the round robin algorithm (getNextWorker)

                        ObjectOutputStream out = getNextWorker(WorkerToMaster.workersOUT);
                        try {
                            out.writeObject(key); //sending the user name and the file name to worker
                            out.writeObject(chunk); //sending the waypoint list to worker
                            out.flush();
                        }
                        catch (IOException e) {
                            e.getMessage();
                        }
                        catch (Exception e) {
                            e.getMessage();
                        }

                    }

                    synchronized (MasterToUser.user_wpts) {
                        MasterToUser.user_wpts.remove(key); //remove the element from the hashmap since it has been assigned to workers
                    }
                }
            }
        }
    }

    ObjectOutputStream getNextWorker(List<ObjectOutputStream> workers) { //round robin algorithm
        ObjectOutputStream worker = workers.get(currentIndex);
        currentIndex = (++currentIndex) % workers.size();
        return worker;
    }

    List<List<Waypoint>> getChunks(List<Waypoint> waypoints) {  // read the file and split it into chunks
        List<List<Waypoint>> chunks = new ArrayList<>(); //create a list that includes a list from waypoints
        int size = waypoints.size();
        int pointer = 0;
        int i;
        List<Waypoint> wpt = new ArrayList<>();  //create the list of waypoints that will be added to chunks
        while (size > 10){
            if (pointer!=0){
                wpt.add(waypoints.get(pointer-1));
            }
            for (i=pointer; i<pointer+10; i++) {
                wpt.add(waypoints.get(i)); //add 10 waypoints
            }
            chunks.add(new ArrayList<>(wpt)); //add the list to the list
            pointer = pointer + 10;
            size = size - 10;
            wpt.clear();
        }
        if (size != 0){
            if (pointer!=0){
                wpt.add(waypoints.get(pointer-1));
            }
            for (i=pointer; i<waypoints.size(); i++) {
                wpt.add(waypoints.get(i)); //add the rest of the waypoints
            }
            chunks.add(new ArrayList<>(wpt)); //add the list to the list
        }
        wpt.clear();

        return chunks;
    }


}