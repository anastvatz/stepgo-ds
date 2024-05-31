//package com.example.myandroid;

public class Master {

    void Handler(){
        MasterToUser m_u = new MasterToUser(); // creates thread for receiving gpx files from user and
        WorkerToMaster w_m = new WorkerToMaster(); // creates thread to communicate with the workers
        GetChunks assigner = new GetChunks(); // creates thread to split the gpx into chunks
        m_u.start();
        w_m.start();
        assigner.start();
    }

    public static void main(String[] args) {
        Master master = new Master();
        master.Handler();
    }

}