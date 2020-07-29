package com.example.chatapp;


import androidx.appcompat.app.AppCompatActivity;
//import io.reactivex.annotations.NonNull;
import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.database.annotations.Nullable;
import androidx.annotation.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

     private ImageView profileImage;
     private TextView profileName,profileStatus,profileFriends;
     private Button  sendRequestBtn,decline_btn;
     private DatabaseReference profileDataRef;
     private  DatabaseReference friendrequestRef;
     private  DatabaseReference friend_databaseRef;
     private DatabaseReference mNotificationRef;
     private DatabaseReference nRootRef;

     private FirebaseUser mCurrent_user;
      private ProgressDialog progressload;
     private String current_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        profileDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        profileDataRef.keepSynced(true);
        friendrequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        friend_databaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");
        nRootRef = FirebaseDatabase.getInstance().getReference();

        progressload = new ProgressDialog(this);
        progressload.setTitle("Loding Users Data");
        progressload.setMessage("Loading");
        progressload.setCanceledOnTouchOutside(false);
        progressload.show();

        current_state = "not Friend";

        decline_btn = findViewById(R.id.profile_decline_req_btn);
        decline_btn.setVisibility(View.GONE);
        sendRequestBtn = findViewById(R.id.profile_request_btn);
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_displayName);
        profileStatus = findViewById(R.id.profile_status);
        profileFriends = findViewById(R.id.profile_friend);


        profileDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                String DisplayName = dataSnapshot.child("name").getValue().toString();
                String Status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                profileName.setText(DisplayName);
                profileStatus.setText(Status);
                //profileFriends.setText("0");
                if(!image.equals("default"))
                        Picasso.get().load(image).into(profileImage);
                else
                        Picasso.get().load(R.drawable.avatar).into(profileImage);



                friend_databaseRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int total_friend = (int)dataSnapshot.getChildrenCount();
                         profileFriends.setText("Total Friend: " + total_friend);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //--------Friends Request Features

                        friendrequestRef.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user_id)) {
                                    String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                                    if (request_type.equals("received")) {
                                        current_state = "request_received";
                                        sendRequestBtn.setText("Accept Friend Request");
                                        sendRequestBtn.setBackgroundColor(Color.parseColor("#0000ff"));
                                        decline_btn.setVisibility(View.VISIBLE);
                                        decline_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                friendrequestRef.child(mCurrent_user.getUid()).child(user_id)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        friendrequestRef.child(user_id).child(mCurrent_user.getUid())
                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                sendRequestBtn.setEnabled(true);
                                                                current_state = "not Friend";
                                                                sendRequestBtn.setText("Send Friend Request");
                                                                sendRequestBtn.setBackgroundColor(Color.parseColor("#FF0000"));
                                                                decline_btn.setVisibility(View.GONE);
                                                            }
                                                        });
                                                    }
                                                });

                                            }
                                        });

                                    } else if (request_type.equals("sent")) {
                                        current_state = "request_sent";
                                        sendRequestBtn.setText("Cancel Friend Request");
                                        sendRequestBtn.setBackgroundColor(Color.parseColor("#D3C7C7"));
                                    }
                                    progressload.dismiss();
                                } else {
                                    friend_databaseRef.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        //     int total_friend = (int)dataSnapshot.getChildrenCount();
                                          //   profileFriends.setText("Total Friend: " + total_friend);
                                            if (dataSnapshot.hasChild(user_id)) {


                                                current_state = "Friend";
                                                sendRequestBtn.setText("Unfriend");
                                                sendRequestBtn.setBackgroundColor(Color.parseColor("#00FF00"));


                                            }
                                            progressload.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progressload.dismiss();
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


     sendRequestBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             sendRequestBtn.setEnabled(false);
     //---------------------to Send Request
             if(current_state.equals("not Friend"))
             {
                 Map requestMap = new HashMap();
                 requestMap.put(mCurrent_user.getUid() + "/" + user_id +  "/request_type","sent");
                 requestMap.put(user_id+"/"+mCurrent_user.getUid() + "/request_type","received");

                  friendrequestRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                              @Override
                              public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                  if(databaseError == null) {
                                      HashMap<String, String> notification = new HashMap<>();
                                      notification.put("from", mCurrent_user.getUid());
                                      notification.put("type", "Request");


                                      mNotificationRef.child(user_id).push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {

                                              sendRequestBtn.setEnabled(true);
                                              current_state = "request_sent";
                                              sendRequestBtn.setText("Cancel Friend Request");
                                              sendRequestBtn.setBackgroundColor(Color.parseColor("#D3C7C7"));
                                              Toast.makeText(ProfileActivity.this, "Request Sent Successfully]", Toast.LENGTH_SHORT).show();

                                          }
                                      });

                                  }


                              }
                          });

             }

          //--------------To Cancel Request
          if(current_state.equals("request_sent"))
          {   sendRequestBtn.setEnabled(false);

              Map requestMap = new HashMap();
              requestMap.put(mCurrent_user.getUid() + "/" + user_id , null);
              requestMap.put(user_id+"/"+mCurrent_user.getUid(), null);


              friendrequestRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                  @Override
                  public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError == null) {
                            sendRequestBtn.setEnabled(true);
                            current_state = "not Friend";
                            sendRequestBtn.setText("Send Friend Request");
                            sendRequestBtn.setBackgroundColor(Color.parseColor("#FF0000"));

                        }

                  }
              });
          }


          // ---------request received features
             if(current_state.equals("request_received"))
             {   final String  currentDate  = DateFormat.getDateTimeInstance().format(new Date());
                 Map requestMap = new HashMap();
                 requestMap.put("Friends/"+mCurrent_user.getUid() + "/" + user_id+"/date", currentDate);
                 requestMap.put("Friends/"+user_id+"/"+mCurrent_user.getUid()+"/date", currentDate);

                 requestMap.put("Friend_request/"+mCurrent_user.getUid() + "/" + user_id , null);
                 requestMap.put("Friend_request/" +user_id+"/"+mCurrent_user.getUid(), null);

                 nRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                     @Override
                     public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                         if (databaseError == null) {
                             sendRequestBtn.setEnabled(true);
                             current_state = "Friend";
                             sendRequestBtn.setText("Unfriend");
                             sendRequestBtn.setBackgroundColor(Color.parseColor("#00FF00"));
                             decline_btn.setVisibility(View.GONE);
                         }
                     }
                 });

             }

             if(current_state.equals("Friend"))
             {
                 Map requestMap = new HashMap();
                 requestMap.put("Friends/"+mCurrent_user.getUid() + "/" + user_id , null);
                 requestMap.put("Friends/"+user_id+"/"+mCurrent_user.getUid(), null);

                 nRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                     @Override
                     public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                         if (databaseError == null) {
                             sendRequestBtn.setEnabled(true);
                             current_state = "not Friend";
                             sendRequestBtn.setText("Send Friend Request");
                             sendRequestBtn.setBackgroundColor(Color.parseColor("#FF0000"));

                         }
                     }
                 });


             }
         }
     });
    }

}
