package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends FirebaseRecyclerAdapter<Users,UserAdapter.UserViewhHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirebaseRecyclerOptions<Users> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewhHolder holder, int i, @NonNull Users users) {
        holder.tvname.setText(users.getName());
        holder.tvstatus.setText(users.getStatus());


    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public UserViewhHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout,parent,false);
        return new UserViewhHolder(view);
    }

    public class UserViewhHolder extends RecyclerView.ViewHolder{
        TextView  tvname,tvstatus;
        ImageView imProfile;

        public UserViewhHolder(@NonNull View itemView) {
            super(itemView);

            tvname = itemView.findViewById(R.id.user_single_name);
            tvstatus = itemView.findViewById(R.id.user_single_status);
            imProfile  = itemView.findViewById(R.id.user_single_image);
        }
    }

}
