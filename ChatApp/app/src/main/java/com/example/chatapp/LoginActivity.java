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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.widget.Toolbar;

public class LoginActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextInputLayout nLoginEmail;
    TextInputLayout nLoginPassword;
    Button Login_btn;

    ProgressDialog LoginProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference userdataref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.login_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Log In");

        LoginProgress = new ProgressDialog(this);

        nLoginEmail = findViewById(R.id.log_email);
        nLoginPassword = findViewById(R.id.log_password);
        Login_btn = findViewById(R.id.login_btn);

        mAuth = FirebaseAuth.getInstance();
        userdataref = FirebaseDatabase.getInstance().getReference().child("Users");


        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = nLoginEmail.getEditText().getText().toString();
                String Password =  nLoginPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(Email) || !TextUtils.isEmpty(Password))
                {
                    LoginProgress.setTitle("Logging In");
                    LoginProgress.setMessage("Please Wait, Checking Credentials");
                    LoginProgress.setCanceledOnTouchOutside(false);
                    LoginProgress.show();
                    LoginUser(Email,Password);


                }
            }
        });

    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            LoginProgress.dismiss();

                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            String current_user_id = mAuth.getCurrentUser().getUid();

                            userdataref.child(current_user_id).child("device_token").setValue(device_token)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(LoginActivity.this,"Logged In",Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            LoginProgress.hide();
                            Toast.makeText(LoginActivity.this,"Cannot Log In, Try Again!",Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


    }
}
