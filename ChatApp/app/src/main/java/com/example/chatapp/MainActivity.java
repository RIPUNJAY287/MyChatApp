package com.example.chatapp;





import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.nio.channels.SelectableChannel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import static com.example.chatapp.R.menu.main_menu;
import static com.google.firebase.auth.FirebaseAuth.getInstance;


public class MainActivity extends AppCompatActivity{
  private FirebaseAuth mAuth;
  private Toolbar eToolbar;
  private ViewPager nViewPager;

  private SectionPageAdapter sectionPageAdapter;
  private DatabaseReference mUserDataRef;
  private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mAuth = FirebaseAuth.getInstance();
            eToolbar = findViewById(R.id.main_page_toolbar);
            setSupportActionBar(eToolbar);
            getSupportActionBar().setTitle("MyChatApp");

            if(mAuth.getCurrentUser() != null) {
                mUserDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            }
            nViewPager= findViewById(R.id.main_tabPager);
            sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

            nViewPager.setAdapter(sectionPageAdapter);
            tabLayout = findViewById(R.id.main_tabs);
            tabLayout.setupWithViewPager(nViewPager);

        }


    @Override
  public void onStart() {
    super.onStart();

    FirebaseUser currentUser = mAuth.getCurrentUser();
    if(currentUser == null)
    {
      sendToStart();
    }
    else{
        mUserDataRef.child("online").setValue("true");
    }
  }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
          //  mUserDataRef.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }

    private void sendToStart() {
    Intent startIntent = new Intent(MainActivity.this,Activitystart.class);
    startActivity(startIntent);
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main_menu,menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    if(item.getItemId() == R.id.main_logout_btn){
        mUserDataRef.child("online").setValue(ServerValue.TIMESTAMP);
           FirebaseAuth.getInstance().signOut();
           sendToStart();
    }
    if(item.getItemId() == R.id.main_account_setting)
    {
        Intent settingIntent = new Intent(MainActivity.this,SettingActivity.class);
         startActivity(settingIntent);
    }
    if(item.getItemId() == R.id.main_all_users)
    {
        Intent userIntent = new Intent(MainActivity.this,UsersActivity.class);
        startActivity(userIntent);
    }
    return true;
  }
}