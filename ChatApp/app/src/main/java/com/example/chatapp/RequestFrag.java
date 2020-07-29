package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFrag extends Fragment {

    private RecyclerView nFriendsReqList;
    private DatabaseReference nFriendsDataRef;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View nMainReqView;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference mUserDataRef;
    public RequestFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        nMainReqView = inflater.inflate(R.layout.fragment_request, container, false);

        nFriendsReqList = (RecyclerView) nMainReqView.findViewById(R.id.friends_req_list);
        mAuth = FirebaseAuth.getInstance();
        mUserDataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        nFriendsDataRef = FirebaseDatabase.getInstance().getReference().child("Friend_request").child(mCurrent_user_id);
        nFriendsDataRef.keepSynced(true);
        mUserDataRef.keepSynced(true);
        nFriendsReqList.setHasFixedSize(true);
        nFriendsReqList.setLayoutManager(new LinearLayoutManager(getContext()));
        Fetch();
        return nMainReqView;
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
                .child("Friend_request/" + mCurrent_user_id);

        final FirebaseRecyclerOptions<FriendRequest> options =
                new FirebaseRecyclerOptions.Builder<FriendRequest>()
                        .setQuery(query, FriendRequest.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<FriendRequest, RequestFrag.ReqViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestFrag.ReqViewHolder friendsViewHolder, int i, @NonNull final FriendRequest friendReq) {

                String request_type = friendReq.getRequest_type();

                if (request_type.equals("received")) {

                final String list_user_id = getRef(i).getKey();
                mUserDataRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String user_name = dataSnapshot.child("name").getValue().toString();
                        String user_thumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String user_online = dataSnapshot.child("online").getValue().toString();

                        String user_status = dataSnapshot.child("status").getValue().toString();
                        friendsViewHolder.userStatusView.setText(user_status);

                        friendsViewHolder.userStatusView.setText(user_status);

                        friendsViewHolder.userNameView.setText(user_name);
                        friendsViewHolder.setImage(user_thumb);
                        friendsViewHolder.setUserOnline(user_online);

                        friendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                startActivity(profileIntent);


                            }


                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


            }


            @Override
            public RequestFrag.ReqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_layout, parent, false);

                return new RequestFrag.ReqViewHolder(view);
            }


        };
        nFriendsReqList.setAdapter(adapter);



    }
    public class ReqViewHolder extends RecyclerView.ViewHolder {
        View nView;
        TextView userNameView;
        TextView userStatusView;
        CircleImageView thumb_image;
        ImageView userOnline;
        public ReqViewHolder(@NonNull View itemView) {
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
