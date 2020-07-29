package com.example.chatapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;


import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.annotations.NonNull;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> nMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataRef;

    public MessageAdapter(List<Messages> nMessagesList){
        this.nMessagesList = nMessagesList;
    }


    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view  = LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.message_single_layout,parent,false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {

             mAuth = FirebaseAuth.getInstance();
            String current_user_id = mAuth.getCurrentUser().getUid();

            Messages C = nMessagesList.get(position);

            String from_user = C.getFrom();
            String message_type = C.getType();

           if(message_type.equals("text")) {
               holder.messageImage.setVisibility(View.GONE);
               if (from_user.equals(current_user_id)) {

                 //  holder.profileImage.setVisibility(View.GONE);
                   holder.messageText.setVisibility(View.GONE);
                   holder.messageUserText.setVisibility(View.VISIBLE);

                   holder.messageUserText.setText(C.getMessage());

               } else {
                   // holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                   //holder.messageText.setTextColor(Color.WHITE);

                   holder.messageUserText.setVisibility(View.GONE);
                   holder.messageText.setVisibility(View.VISIBLE);
                   //holder.profileImage.setVisibility(View.VISIBLE);

                   holder.messageText.setText(C.getMessage());

               }

           }
           else{


               if (from_user.equals(current_user_id)) {

                //   holder.profileImage.setVisibility(View.GONE);
                   holder.messageImage.setBackgroundResource(R.drawable.message_text_user_background);


               } else {
                   // holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                   //holder.messageText.setTextColor(Color.WHITE);

                  // holder.profileImage.setVisibility(View.VISIBLE);
                   holder.messageImage.setBackgroundResource(R.drawable.message_text_background);



               }
               holder.messageImage.setVisibility(View.VISIBLE);
               holder.messageText.setVisibility(View.GONE);
               holder.messageUserText.setVisibility(View.GONE);
               Picasso.get().load(C.getMessage()).into(holder.messageImage);


           }

    }
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return nMessagesList.size();
    }

    public class MessageViewHolder extends  RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView messageUserText;
     //   public CircleImageView profileImage;
        public ImageView messageImage;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_layout);
          //  profileImage = itemView.findViewById(R.id.message_profile_layout);
            messageUserText = itemView.findViewById(R.id.message_text_user_layout);
            messageImage = itemView.findViewById(R.id.message_image_layout);
        }
    }
}
