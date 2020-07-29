package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.annotations.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFrag extends Fragment {

    private RecyclerView nFriendsChatList;
    private DatabaseReference nFriendsDataRef;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View nMainChatView;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference mUserDataRef;
    private DatabaseReference nRootRef;
    public ChatFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nMainChatView = inflater.inflate(R.layout.fragment_chat, container, false);

        nFriendsChatList = (RecyclerView) nMainChatView.findViewById(R.id.friends_chat_list);
        mAuth = FirebaseAuth.getInstance();
        mUserDataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        nFriendsDataRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        nRootRef = FirebaseDatabase.getInstance().getReference();
        nFriendsDataRef.keepSynced(true);
        mUserDataRef.keepSynced(true);
        nFriendsChatList.setHasFixedSize(true);
        nFriendsChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        Fetch();
        return nMainChatView;

    }

    @Override
    public void onStart() {

        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {

        super.onStop();
        adapter.stopListening();
    }
    public void Fetch() {
        super.onStart();


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends/"+ mCurrent_user_id);

        final FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Friends, ChatFrag.ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatFrag.ChatViewHolder friendsViewHolder, int i, @NonNull final Friends friends) {


                final String list_user_id = getRef(i).getKey();


                mUserDataRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        final String user_name= dataSnapshot.child("name").getValue().toString();
                        String  user_thumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String user_online = dataSnapshot.child("online").getValue().toString();

                        //String user_status = dataSnapshot.child("status").getValue().toString();

                        friendsViewHolder.userNameView.setText(user_name);
                        friendsViewHolder.setImage(user_thumb);
                        friendsViewHolder.setUserOnline(user_online);
                        friendsViewHolder.userStatusView.setText("Tap to Message");
                        friendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getContext(), ChatActivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                profileIntent.putExtra("user_name", user_name);
                                startActivity(profileIntent);


                            }

                        });




             /*           DatabaseReference messageRef =  nRootRef.child("messages").child(mCurrent_user_id).child(list_user_id);
                        Query messageQuery  = messageRef.limitToLast(1);

                        messageQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                Messages messages = dataSnapshot.getValue(Messages.class);
                               // String messageKey =  dataSnapshot.getKey();

                                String lastMesssage  =  messages.getMessage();
                                String msg_type = messages.getType();
                                if(msg_type.equals("text")) {
                                    friendsViewHolder.userStatusView.setText(lastMesssage);
                                }
                                else if(msg_type.equals("image")){
                                    friendsViewHolder.userStatusView.setText("img");

                                }
                                else{
                                    friendsViewHolder.userStatusView.setText("Say Hi! to your Friend");

                                }

                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(),"msg is added",Toast.LENGTH_SHORT);


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

*/


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


            @Override
            public ChatFrag.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_layout, parent, false);

                return new ChatFrag.ChatViewHolder(view);
            }


        };
        nFriendsChatList.setAdapter(adapter);
    }


    public class ChatViewHolder extends RecyclerView.ViewHolder {
        View nView;
        TextView userNameView;
        TextView userStatusView;
        CircleImageView thumb_image;
        ImageView userOnline;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            nView  = itemView;
            userNameView = itemView.findViewById(R.id.user_single_name);
            userStatusView = itemView.findViewById(R.id.user_single_status);
            thumb_image = itemView.findViewById(R.id.user_single_image);

        }
        public void setImage(String imgName){
            if(!imgName.equals("default"))
                Picasso.get().load(imgName).placeholder(R.drawable.avatar).into(thumb_image);
            else
                Picasso.get().load(R.drawable.avatar).into(thumb_image);

        }
        public  void setUserOnline(String online_status){
            userOnline = itemView.findViewById(R.id.user_single_online);
            if(online_status.equals("true")){
                userOnline.setVisibility(View.VISIBLE);
            }
            else{

                userOnline.setVisibility(View.INVISIBLE);
            }

        }

    }

}
