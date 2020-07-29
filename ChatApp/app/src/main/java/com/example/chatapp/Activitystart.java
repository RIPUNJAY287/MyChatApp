package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activitystart extends AppCompatActivity {
    private Button nRegbtn,nlogbtn;
    private FirebaseDatabase database;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitystart);


        nlogbtn = findViewById(R.id.start_login_btn);
        nlogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Activitystart.this,LoginActivity.class);
                startActivity(intent);
            }
        });

      nRegbtn  = findViewById(R.id.start_reg_btn);
      nRegbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(Activitystart.this,RegisterActivity.class);
              startActivity(intent);
          }
      });

    }
}
