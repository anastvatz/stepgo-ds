package com.example.myandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity2 extends AppCompatActivity {

    Button btn;

    EditText input;

    TextView label;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                String result = message.getData().getString("result");

                label.setText(result);

                return true;
            }
        });

        btn = findViewById(R.id.button);
        input = findViewById(R.id.input);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = input.getText().toString();
                Toast.makeText(MainActivity2.this,"GPX file: "+ txt+", sent successfully",Toast.LENGTH_LONG).show();

                String username = getIntent().getStringExtra("KEY_INPUT_DATA");
                User u1 = new User(username,txt,handler);
                u1.start();

                Intent i = new Intent(MainActivity2.this, Statistics.class);
                startActivity(i);

            }
        });
    }
}