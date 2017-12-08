package com.example.yan_c_000.auth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Hallfinder extends AppCompatActivity {

    private TextView tet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hallfinder);
        tet = (TextView) findViewById(R.id.textView);
        //tet.append(MainActivity.idToken);

    }
}
