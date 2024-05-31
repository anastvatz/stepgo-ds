//package com.example.myandroid;
import java.io.*;
import java.net.*;
import java.util.*;
public class Listener extends Thread {
    ObjectInputStream in;
    
    static HashMap<String,List<List<Double>>> database = new HashMap<>();
    static HashMap<String,List<List<Double>>> user_statistics = new HashMap<>();

    public Listener(ObjectInputStream in){
        this.in = in;
    }

    public void run(){
        try{
            while(true) {

                String user_file = (String) in.readObject(); //reads the user name and the file name from the worker
                List<Double> statistics = (List<Double>) in.readObject(); //reads the list of statistics that are sent from the worker

                String user = user_file.split(" ")[0]; //splits the string value into two
                String file = user_file.split(" ")[1];

                synchronized (database) {
                    if (database.containsKey(user_file)){   //if the database contains the pair of user and file name as key
                        database.get(user_file).add(statistics); //adding the statistics of the new chunk
                        System.out.println("Element added to existing list!");
                    }
                    else{ //otherwise we create new list that contains the statistics of the file
                        List<List<Double>> Stats = new ArrayList<>();
                        Stats.add(statistics);
                        database.put(user_file, Stats);
                        System.out.println("New element added!");
                    }   }
                synchronized (GetChunks.file_chunks) {
                    if (GetChunks.file_chunks.containsKey(file)) { //checking if the pair of user file exists in file chunks
                        try {
                            int t = GetChunks.file_chunks.get(file)-1; //if we find the pair we reduce the number of the file chunks by 1
                            GetChunks.file_chunks.replace(file, t);  //providing that the input is one chunk at a time
                        }
                        catch (Exception e) {System.err.println("??? "+file +" "+e.getMessage());}
                    }
                }
                //synchronized and if condition check
                synchronized (GetChunks.file_chunks) {
                    if (GetChunks.file_chunks.containsKey(file) && GetChunks.file_chunks.get(file) == 0){ //we go to the reduce phase after all the results from the chunks of the file are collected
                        List <List<Double>> results = new ArrayList<>();
                        for (List<Double> list : database.get(user_file)) {
                            results.add(new ArrayList<>(list));
                        }
                        database.remove(user_file); //removing the pair from the database
                        Reduce reduce = new Reduce(user, results);
                        reduce.start();
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                in.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    //////////////////REDUCE PHASE///////////////////////
    private class Reduce extends Thread {

        List<List<Double>> database;
        String user;
        double distance = 0, time = 0, elevation = 0, speed = 0;

        public Reduce(String user, List<List<Double>> database){
            this.database = database;
            this.user = user;
        }

        public void run() {
            for (int i = 0; i < database.size() ; i++) {
                distance += database.get(i).get(0);
                time += database.get(i).get(1);
                elevation += database.get(i).get(2);


            }

            speed = distance / time;
            System.out.println("THE DISTANCE IS " + distance);
            System.out.println("THE TIME IS " + time);
            System.out.println("THE ELEVATION IS " + elevation);
            System.out.println(" ");

            System.out.println("THE SPEED IS " + speed);

            List<Double> total_statistics = new ArrayList<>();
            total_statistics.add(distance);
            total_statistics.add(time);
            total_statistics.add(elevation);
            total_statistics.add(speed);
           

            if (user_statistics.containsKey(user)){
                user_statistics.get(user).add(total_statistics);
            }
            else{
                List<List<Double>> Statistics = new ArrayList<>();
                Statistics.add(total_statistics);
                user_statistics.put(user, Statistics);
            }

            System.out.println("TOTAL STATISTICS UPDATED");
            System.out.println(user_statistics.get(user).size());
            
            Socket clientSocket = MasterToUser.user_socket.get(user);
            try{

                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                synchronized (outputStream) {
                    outputStream.writeObject(total_statistics);
                    outputStream.writeObject(user_statistics);
                    outputStream.flush();
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
