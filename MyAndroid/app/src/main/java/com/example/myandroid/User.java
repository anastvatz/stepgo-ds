
package com.example.myandroid;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class User extends Thread{

    String arg;
    String pathname = "/storage/emulated/0/Download/";
    Handler handler;
    static List<Double> total_statistics;
    static String gpx_file;
    static String name;
    static HashMap<String,List<List<Double>>> user_statistics;

    public User(String name, String gpx_file, Handler handler){
        User.name = name;
        User.gpx_file = gpx_file+".gpx";
        this.handler = handler;
    }

    @Override
    public void run() {

        Socket requestSocket= null ;
        PrintWriter out = null;
        ObjectInputStream in = null ; //reads the gpx files from the user


        try {
            // Write IP
            String host = "10.26.49.189";

            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket(host , 4321);

            out = new PrintWriter(requestSocket.getOutputStream(), true);

            File file = new File(pathname+gpx_file);
            String fileName = file.getName();  // Get the name of the file
            out.println(fileName);  // Send the name of the file through the socket to Master (MasterToUser)
            out.flush();
            try (BufferedReader reader = new BufferedReader(new FileReader(pathname+gpx_file))) { //reading the gpx
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line); // Send each line from the GPX file to the Master via socket
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            out.flush();

            //System.out.println("User CONNECTED TO MASTER!!");
            // Receive the result from the server
            try {
                while (true){
                    in = new ObjectInputStream(requestSocket.getInputStream());
                    System.out.println("IN TRUE");

                    total_statistics = (List<Double>) in.readObject();
                    user_statistics = (HashMap<String,List<List<Double>>>) in.readObject();
                    System.out.println("TOTAL STATISTICS RECEIVED");

                    System.out.printf("%s %.2f%n", "The total distance was", total_statistics.get(0));
                    System.out.printf("%s %.2f%n", "The total time was", total_statistics.get(1));
                    System.out.printf("%s %.2f%n", "The total elevation was", total_statistics.get(2));
                    System.out.printf("%s %.2f%n", "The total speed was", (double)total_statistics.get(3));

                }
            } catch (StreamCorruptedException e) {
                // Handle the exception
                System.err.println("Please retry");
            } catch (SocketException e) {
                System.err.print("");
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }


    }
}