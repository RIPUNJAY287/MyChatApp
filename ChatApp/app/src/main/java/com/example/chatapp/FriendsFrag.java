package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
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

import org.w3c.dom.Text;


public class FriendsFrag extends Fragment {

    private RecyclerView nFriendsList;
    private DatabaseReference nFriendsDataRef;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View nMainView;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference mUserDataRef;

    public FriendsFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        nFriendsList = (RecyclerView) nMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        mUserDataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        nFriendsDataRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        nFriendsDataRef.keepSynced(true);
       mUserDataRef.keepSynced(true);
        nFriendsList.setHasFixedSize(true);
        nFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        Fetch();
        return nMainView;

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

        adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int i, @NonNull final Friends friends) {


              final String list_user_id = getRef(i).getKey();
              mUserDataRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      final String user_name= dataSnapshot.child("name").getValue().toString();
                      String  user_thumb = dataSnapshot.child("thumb_image").getValue().toString();

                      String user_online = dataSnapshot.child("online").getValue().toString();
                      String user_status = dataSnapshot.child("status").getValue().toString();
                      friendsViewHolder.userStatusView.setText(user_status);

                      friendsViewHolder.userNameView.setText(user_name);
                      friendsViewHolder.setImage(user_thumb);
                     friendsViewHolder.setUserOnline(user_online);

                     friendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             CharSequence options[] = new CharSequence[]{"Open Profile","Send Message"};
                             AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                             builder.setTitle("Select Option");
                             builder.setItems(options, new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                   if(which == 0)
                                   {
                                       Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                       profileIntent.putExtra("user_id",list_user_id);
                                       startActivity(profileIntent);

                                   }
                                   else if(which == 1){

                                       Intent profileIntent = new Intent(getContext(), ChatActivity.class);
                                       profileIntent.putExtra("user_id",list_user_id);
                                       profileIntent.putExtra("user_name",user_name);
                                       startActivity(profileIntent);



                                   }
                                 }
                             });
                             builder.show();
                         }
                     });

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });

            }

            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_layout, parent, false);

                return new FriendsViewHolder(view);
            }


        };
        nFriendsList.setAdapter(adapter);
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View nView;
        TextView userNameView;
        TextView userStatusView;
        CircleImageView thumb_image;
        ImageView userOnline;
        public FriendsViewHolder(@NonNull View itemView) {
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