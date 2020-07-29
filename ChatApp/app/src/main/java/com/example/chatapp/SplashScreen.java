package com.example.chatapp;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    Animation top_anim,left_anim,right_anim;
    Animation back_anim;
    ImageView image_white,image_violet;
    TextView tvWel,creator_name,tvconnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

      top_anim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        back_anim = AnimationUtils.loadAnimation(this,R.anim.back_animation);
        left_anim = AnimationUtils.loadAnimation(this, R.anim.left_animation);
        right_anim = AnimationUtils.loadAnimation(this,R.anim.right_animation);

        image_white = findViewById(R.id.splash_white_image);
        image_violet = findViewById(R.id.splash_violet_image);
        tvWel = findViewById(R.id.tvSplash);
        creator_name= findViewById(R.id.splash_creator);
        tvconnection = findViewById(R.id.tvsplah_connect);

        image_violet.setAnimation(right_anim);
        image_white.setAnimation(left_anim);
        tvWel.setAnimation(back_anim);
        tvconnection.setAnimation(top_anim);
         creator_name.setAnimation(back_anim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        },3000);

    }
}
