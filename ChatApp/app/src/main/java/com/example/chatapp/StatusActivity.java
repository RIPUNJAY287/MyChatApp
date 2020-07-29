package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout setStatus;
    private Button save_status_btn;
    private DatabaseReference databaseRef;
    private FirebaseUser currentuser;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = currentuser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


      toolbar = (Toolbar) findViewById(R.id.status_app_bar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle("Account Status");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      String pre_status = getIntent().getStringExtra("previous_status");

      setStatus = findViewById(R.id.status_input);
      setStatus.getEditText().setText(pre_status);
      save_status_btn = findViewById(R.id.save_status_btn);
      save_status_btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              progressDialog = new ProgressDialog(StatusActivity.this);
              progressDialog.setTitle("Saving Changes");
              progressDialog.setMessage("Please Wait while saving changes");
              progressDialog.setCanceledOnTouchOutside(false);
              progressDialog.show();

              String status =  setStatus.getEditText().getText().toString();
              databaseRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful())
                      {
                          progressDialog.dismiss();
                          Toast.makeText(getApplicationContext(),"Status Changed",Toast.LENGTH_SHORT).show();
                          setStatus.getEditText().setText("");

                      }
                      else{
                          progressDialog.hide();
                          Toast.makeText(getApplicationContext(),"some error in changes",Toast.LENGTH_SHORT).show();
                      }
                  }
              });


          }
      });
    }
}
