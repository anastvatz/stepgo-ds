//package com.example.myandroid;
import java.io.*;
import java.net.*;
import java.util.*;

public class WorkerToMaster extends Thread {

    /* Define the socket that receives requests */
    ServerSocket s;
    /* Define the socket that is used to handle the connection */
    Socket providerSocket;
    int currentIndex = 0;
    static List<ObjectInputStream> workersIN = new ArrayList<>();
    static List<ObjectOutputStream> workersOUT = new ArrayList<>();

    public WorkerToMaster(){}

    public void run() {

        try {

            /* Create Server Socket */
            s = new ServerSocket(4322, 500);

            while (true) {
                /* Accept the connection */
                providerSocket = s.accept();
                ObjectOutputStream out = new ObjectOutputStream(providerSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(providerSocket.getInputStream());
                workersIN.add(in); workersOUT.add(out); //list to save the ObjectInputStream and ObjectOutputStream for the workers
                Listener listen = new Listener(in);
                listen.start(); // open an always listening thread for incoming results from workers
                System.out.println("WORKER CONNECTED!!");
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}