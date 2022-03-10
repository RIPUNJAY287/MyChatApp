package com.example.chatapp;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {
    static{
        System.loadLibrary("keys");
    }
    private native  String getSecretKey();
    private native  String getSecretIV();

    private String mChatUser;
    private Toolbar mChatToolbar;
    private DatabaseReference nRootRef;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private ImageButton nChatAddbtn;
    private ImageButton nChatSendbtn;
    private EditText nChatMessageView;

    private RecyclerView nMessagesList;
    private SwipeRefreshLayout nSwipeLayout;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private static final int Total_Item_To_Load = 10;
    private int currentPage = 1;
    private int itemPos = 0;
    private String nLastKey = "";
    private String nPrewKey = "";
    private static final int GALLERY_PICK = 44;

    ProgressDialog progressdia;
    AES aes;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id");
        String user_name = getIntent().getStringExtra("user_name");


        nChatAddbtn = findViewById(R.id.chat_add_btn);
        nChatSendbtn = findViewById(R.id.chat_send_btn);
        nChatMessageView = findViewById(R.id.chat_message_view);

        messageAdapter = new MessageAdapter(messagesList);

        nMessagesList = findViewById(R.id.messages_list);
        nSwipeLayout = findViewById(R.id.message_swipe_layout);
        linearLayoutManager = new LinearLayoutManager(this);
        nMessagesList.setHasFixedSize(true);
        nMessagesList.setLayoutManager(linearLayoutManager);
        nMessagesList.setAdapter(messageAdapter);

        try {
            aes = new AES();
        }
        catch(Exception ignored) {
            ignored.printStackTrace();
        }

        loadMessages();


        mChatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        getSupportActionBar().setTitle(user_name);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar_View = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(actionbar_View);

        // -----custom Action bar Item
        mTitleView = findViewById(R.id.custom_bar_title);
        mLastSeenView = findViewById(R.id.custom_bar_seen);
        mProfileImage = findViewById(R.id.custom_bar_image);

        mTitleView.setText(user_name);
        nRootRef.child("Users").child(mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                Picasso.get().load(image).into(mProfileImage);
                if (online.equals("true")) {
                    mLastSeenView.setText("Online");
                } else {
                    GetTimeAgo getTimeago = new GetTimeAgo();
                    long lasttime = Long.parseLong(online);
                    String lastSeenTime = getTimeago.getTimeAgo(lasttime, getApplicationContext());
                    mLastSeenView.setText(lastSeenTime);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map ChatAdMap = new HashMap();
                    ChatAdMap.put("seen", false);
                    ChatAdMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map ChatUserMap = new HashMap();
                    ChatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, ChatAdMap);
                    ChatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, ChatAdMap);

                    nRootRef.updateChildren(ChatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Log.d("Chat_Log", databaseError.getMessage().toString());
                            }
                            nChatMessageView.setText("");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nChatSendbtn.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        nSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });

        nChatAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery_intent, "Select image"), GALLERY_PICK);

            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode ==RESULT_OK){
            Uri imageUri = data.getData();
           Log.d("image uri for chat activity is here", imageUri.toString());
            final String current_user_ref = "messages/"+ mCurrentUserId + "/"+mChatUser;
            final String chat_user_ref = "messages/"+mChatUser +"/" + mCurrentUserId;
            DatabaseReference user_message_push = nRootRef.child("messgaes").child(mCurrentUserId)
                    .child(mChatUser).push();
            final String push_id = user_message_push.getKey();

            final StorageReference filepath = FirebaseStorage.getInstance().getReference()
                    .child("messages_images").child(push_id+".jpg");

            progressdia = new ProgressDialog(ChatActivity.this);
            progressdia.setTitle("Uploading Image");
            progressdia.setMessage("Please Wait!");
            progressdia.setCanceledOnTouchOutside(false);
            progressdia.show();
         /*
            File image_file = new File(imageUri.getPath());
            Bitmap compressedbitmap = null;
            try {
                compressedbitmap = new Compressor(ChatActivity.this)
                        .setMaxHeight(100)
                        .setMaxWidth(100)
                        .setQuality(50)
                        .compressToBitmap(image_file);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                compressedbitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
                byte[] image_byte = stream.toByteArray();

          */
                filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String download_url = uri.toString();

                                    Map messageMap  = new HashMap();
                                    messageMap.put("message",download_url);
                                    messageMap.put("seen",false);
                                    messageMap.put("type","image");
                                    messageMap.put("time",ServerValue.TIMESTAMP);
                                    messageMap.put("from",mCurrentUserId);

                                    Map messageUserMap = new HashMap();
                                    messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                                    messageUserMap.put(chat_user_ref + "/"+push_id,messageMap);
                                    nChatMessageView.setText("");
                                    nRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                            if(databaseError == null){

                                                progressdia.dismiss();

                                            }
                                        }
                                    });


                                }
                            });
                        }


                    }
                });


        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadMoreMessages() {
        DatabaseReference messageRef =  nRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery  = messageRef.orderByKey().endAt(nLastKey).limitToLast(10);


        byte[] decodedKey = Base64.getDecoder().decode(getSecretKey());
        final SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        messageQuery.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                String messageKey =  dataSnapshot.getKey();


                if(itemPos ==0){
                    nLastKey = messageKey;

                }
                String msgtype = messages.getType();

                String txt= "text";
                if (msgtype.equals(txt)){
                    try {
                        String msgiv = messages.getIV();
                        final byte[] originalIV = Base64.getDecoder().decode(msgiv);

                        messages.setMessage(aes.decrypt(messages.getMessage(),originalKey,originalIV));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(!messageKey.equals(nPrewKey)){
                    messagesList.add(itemPos++,messages);

                }
                else if(messageKey.equals(nPrewKey)){
                    nPrewKey = nLastKey;
                }


                messageAdapter.notifyDataSetChanged();
                linearLayoutManager.scrollToPositionWithOffset(10,0);

                nSwipeLayout.setRefreshing(false);
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadMessages() {
        DatabaseReference messageRef =  nRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery  = messageRef.limitToLast(currentPage * Total_Item_To_Load);

        byte[] decodedKey = Base64.getDecoder().decode(getSecretKey());
        final SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");


        messageQuery.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                itemPos++;
                if (itemPos == 1) {
                    nLastKey = messageKey;
                    nPrewKey = messageKey;
                }

                String msgtype = messages.getType();

                String txt= "text";
                if (msgtype.equals(txt)){
                    try {
                        String msgiv = messages.getIV();
                        final byte[] originalIV = Base64.getDecoder().decode(msgiv);

                        messages.setMessage(aes.decrypt(messages.getMessage(),originalKey,originalIV));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                    messagesList.add(messages);

                    messageAdapter.notifyDataSetChanged();
                    nMessagesList.scrollToPosition(messagesList.size() - 1);
                    nSwipeLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage(){
        String message = nChatMessageView.getText().toString();
        String iv="";
        try{
            byte[] decodedKey = Base64.getDecoder().decode(getSecretKey());
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            String[]  enMessage = new String[2];
            enMessage = aes.encrypt(message,originalKey);
            message = enMessage[0];
            iv = enMessage[1];
        }
        catch(Exception e){
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(message)){
            Map messageMap = new HashMap();
            String current_user_ref = "messages/"+ mCurrentUserId + "/"+mChatUser;
            String chat_user_ref = "messages/"+mChatUser +"/" + mCurrentUserId;

            DatabaseReference user_message_push = nRootRef.child("messages").child(mCurrentUserId)
                    .child(mChatUser).push();

            String push_id = user_message_push.getKey();

            messageMap.put("message",message );
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUserId);
            messageMap.put("IV",iv);


            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref + "/"+push_id,messageMap);

            nRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError != null)
                    {
                        Log.d("Chat_Log",databaseError.getMessage().toString());
                    }
                    nChatMessageView.setText("");
                    nMessagesList.scrollToPosition(messagesList.size() - 1);
                }
            });
        }
    }
}
