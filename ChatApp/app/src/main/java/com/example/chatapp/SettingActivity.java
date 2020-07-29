package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SettingActivity extends AppCompatActivity {
    private CircleImageView imgprofile;
    private DatabaseReference databaseRefer;
    private FirebaseUser currentUser;
    private StorageReference profileImageRef;
    private TextView userName,userStatus;
    private Button  status_btn,image_btn;
    private static final int Gallery_code = 1097;
    ProgressDialog progressdia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        imgprofile = findViewById(R.id.setting_image);

        imgprofile.setImageResource(R.drawable.pubg);

        userName = findViewById(R.id.setting_display_name);
        userStatus = findViewById(R.id.setting_status);

        profileImageRef =  FirebaseStorage.getInstance().getReference();

        status_btn = findViewById(R.id.setting_status_btn);
        image_btn = findViewById(R.id.setting_image_btn);

        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*  CropImage.activity()
                      .setGuidelines(CropImageView.Guidelines.ON)
                      .start(SettingActivity.this);

          */
                CharSequence options[] = new CharSequence[]{"Open Gallary","Set Default"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0)
                        {


                            Intent gallery_intent = new Intent();
                            gallery_intent.setType("image/*");
                            gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(gallery_intent,"Select image"),Gallery_code);

                        }
                        else if(which == 1){

                           Picasso.get().load(R.drawable.avatar).into(imgprofile);


                        }
                    }
                });
                builder.show();














            }
        });
        status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nstatus = userStatus.getText().toString();
                Intent intent  = new Intent(SettingActivity.this,StatusActivity.class);
                intent.putExtra("previous_status",nstatus);
                startActivity(intent);

            }
        });


        currentUser  = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = currentUser.getUid();
        databaseRefer = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        databaseRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(SettingActivity.this,dataSnapshot.toString(),Toast.LENGTH_SHORT).show();

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                // String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                userName.setText(name);
                userStatus.setText(status);
                if(!image.equals("default")) {
                    // Picasso.get().load(image).placeholder(R.drawable.pubg).into(imgprofile);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.avatar).into(imgprofile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });


                }
                else{
                    Picasso.get().load(R.drawable.avatar).into(imgprofile);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == Gallery_code && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }



        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {

                progressdia = new ProgressDialog(SettingActivity.this);
                progressdia.setTitle("Uploading Image");
                progressdia.setMessage("Please Wait!, Profile Pic is Uploading");
                progressdia.setCanceledOnTouchOutside(false);
                progressdia.show();
                Uri resultUri = result.getUri();
                final File thumb_file  =  new File(resultUri.getPath());
                //Toast.makeText(SettingActivity.this,resultUri.toString(),Toast.LENGTH_SHORT).show();
                final String current_user_id = currentUser.getUid();





                final StorageReference filepath = profileImageRef.child("profile_images").child(current_user_id+".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //String download_url = filepath.getDownloadUrl().toString();
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String download_url = uri.toString();


                                    //final String[] download_thumb_url = new String[1];
                                    try {
                                        Bitmap compressedbitmap = new Compressor(SettingActivity.this)
                                                .setMaxHeight(200)
                                                .setMaxWidth(200)
                                                .setQuality(50)
                                                .compressToBitmap(thumb_file);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        compressedbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] thumb_byte = stream.toByteArray();
                                        final StorageReference thumb_filepath = profileImageRef.child("profile_images").child("thumbs").child(current_user_id + ".jpg");
                                        //final UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                        thumb_filepath.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                                if(thumb_task.isSuccessful())
                                                {
                                                    thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            String thumb_url =  uri.toString();
                                                           // Toast.makeText(SettingActivity.this,"Profile Pic Uploaded",Toast.LENGTH_LONG).show();


                                                            Map downloaded_url = new HashMap<>();
                                                            downloaded_url.put("image",thumb_url);
                                                            downloaded_url.put("thumb_image",thumb_url);




                                                            databaseRefer.updateChildren(downloaded_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        progressdia.dismiss();
                                                                        Picasso.get().load(download_url).placeholder(R.drawable.avatar).into(imgprofile);
                                                                        Toast.makeText(SettingActivity.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();

                                                                    }

                                                                }
                                                            });

                                                        }
                                                    });

                                                }
                                            }
                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });


                      /*
                            Toast.makeText(SettingActivity.this,download_url,Toast.LENGTH_LONG).show();
                            //Toast.makeText(SettingActivity.this, "profile pic uploaded", Toast.LENGTH_SHORT).show();
                             databaseRefer.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful())
                                     {
                                         progressdia.dismiss();
                                         Toast.makeText(SettingActivity.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();

                                     }

                                 }
                             });


                       */
                        }
                        else
                        {
                            progressdia.dismiss();
                            Toast.makeText(getApplicationContext(), "error in uploading", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }



}

