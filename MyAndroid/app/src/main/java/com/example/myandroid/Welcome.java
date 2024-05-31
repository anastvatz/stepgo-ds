package com.example.myandroid;

        import static java.lang.Thread.sleep;
        import androidx.appcompat.app.AppCompatActivity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

public class Welcome extends AppCompatActivity {

    Button btn;

    EditText input;

    TextView label;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent homeIntent = new Intent(Welcome.this, MainActivity.class);
                startActivity (homeIntent);
                finish();
            }
        }, 4000);

    }
}
