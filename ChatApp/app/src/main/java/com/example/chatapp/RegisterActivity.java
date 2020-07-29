package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout nDisplayName;
    private TextInputLayout nEmail;
    private TextInputLayout nPassword;
    private Button nCreatebtn;
    private FirebaseAuth mAuth;
    private ProgressDialog RegProgress;
    private DatabaseReference databaseRef;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nDisplayName = findViewById(R.id.reg_display_name);
        nEmail = findViewById(R.id.reg_email);
        nPassword = findViewById(R.id.reg_password);
        nCreatebtn = findViewById(R.id.reg_create_btn);
        mAuth = FirebaseAuth.getInstance();
        RegProgress = new ProgressDialog(this);
        toolbar = (Toolbar)findViewById(R.id.register_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register Your Account");

        nCreatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  display_name = nDisplayName.getEditText().getText().toString();
                String  email = nEmail.getEditText().getText().toString();
                String password = nPassword.getEditText().getText().toString();
              if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                  RegProgress.setTitle("Registering Your Account");
                  RegProgress.setMessage("Wait,  While Your Account is Registering");
                  RegProgress.setCanceledOnTouchOutside(false);
                  RegProgress.show();
                  registerUser(display_name, email, password);

              }
            }
        });
    }

    private void registerUser(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = firebaseUser.getUid();
                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("device_token",device_token);
                            userMap.put("name",display_name);
                            userMap.put("status","Hey There, I am using Chat App");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");

                            databaseRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    RegProgress.dismiss();
                                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            });


                            // Sign in success, update UI with the signed-in user's information

                        } else {
                            RegProgress.hide();
                            // If sign in fails, display a message to the user.
                           Toast.makeText(RegisterActivity.this,"Cannot Register, Try Again!",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });



    }
}
