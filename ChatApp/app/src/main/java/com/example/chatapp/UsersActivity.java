package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
//import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
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

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity   {
    private Toolbar toolbar;
    private RecyclerView Userlist;
    private DatabaseReference dataref;
    private  FirebaseRecyclerAdapter adapter;
    List<Users> allUsers  = new ArrayList<>();
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        toolbar = (Toolbar) findViewById(R.id.users_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All Users");


        dataref = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        Userlist =(RecyclerView) findViewById(R.id.users_list);
        Userlist.setLayoutManager(new LinearLayoutManager(this));
        Userlist.setHasFixedSize(true);

        fetch(null);
    }

    @Override
    protected  void onStart() {

        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {

        super.onStop();
        adapter.stopListening();
    }
    private void fetch(String searchText) {
        Query query;
      if(searchText == null) {
         query  = FirebaseDatabase.getInstance()
                  .getReference()
                  .child("Users");
      }
       else{
          query = dataref.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
          Log.d("query", "fetch: searching query is working"+searchText);
         // query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name").equalTo(searchText);


      }

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_layout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model) {
                allUsers.add(model);
                holder.etname.setText(model.getName());
                holder.etstatus.setText(model.getStatus());// Bind the Chat object to the ChatHolder
                holder.setIvimage(model.getThumb_image());

                final String user_id = getRef(position).getKey();

                holder.vieew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         String current_user = mAuth.getCurrentUser().getUid();
                        if(!user_id.equals(current_user)) {
                            Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("user_id", user_id);
                            startActivity(profileIntent);

                        }
                        else if(user_id.equals(current_user)){
                            Toast.makeText(UsersActivity.this,"You",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // ...
            }
        };


        Userlist.setAdapter(adapter);
    }


    public class UsersViewHolder extends  RecyclerView.ViewHolder{
        TextView etname,etstatus;
        CircleImageView ivimage;
        View vieew;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            vieew = itemView;
            etname = itemView.findViewById(R.id.user_single_name);
            etstatus = itemView.findViewById(R.id.user_single_status);
            ivimage=  itemView.findViewById(R.id.user_single_image);

        }
        public void setIvimage(String thumb_img) {
            if(!thumb_img.equals("default"))
                   Picasso.get().load(thumb_img).into(ivimage);
            else
                 Picasso.get().load(R.drawable.avatar).into(ivimage);
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.search_menu_bar);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetch(query);
                adapter.startListening();
                //adapter.notifyDataSetChanged();
             //   Log.d("Working", "onQueryTextSubmit: ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //getFilter().filter(newText);

                fetch(newText);
                adapter.startListening();
                //adapter.notifyDataSetChanged();
               // Log.d("Working", "onQueryTextChange: ");

                return false;
            }
        });



        return true;
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

        return  true;
    }


    /**
     * <p>Returns a filter that can be used to constrain data with a filtering
     * pattern.</p>
     *
     * <p>This method is usually implemented by {@link Adapter}
     * classes.</p>
     *
     * @return a filter used to constrain data
     */
   /* @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Users> filterUser = new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filterUser.addAll(allUsers);
            }
            else{
                for(Users user:allUsers){
                    if(user.getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterUser.add(user);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterUser;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
           allUsers.clear();
           allUsers.add((Users) results.values);


        }
    };

    */

}
