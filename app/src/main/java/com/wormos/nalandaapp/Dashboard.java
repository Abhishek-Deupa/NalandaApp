package com.wormos.nalandaapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.ak1.BubbleTabBar;
import io.ak1.OnBubbleClickListener;

public class Dashboard extends AppCompatActivity {

    BubbleTabBar bottomNavBar;
    ImageView userProfile;
    Uri profileUri;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Students");
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("/Profile Picture");
    String userEmailConverted;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //initialization
        bottomNavBar = findViewById(R.id.bottom_nav_Bar);
        userProfile = findViewById(R.id.dashboard_profile_photo);
        userEmailConverted = Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("\\.","%7");

        //methodology

        FragmentTransaction dashboardTrans = getSupportFragmentManager().beginTransaction();
        dashboardTrans.replace(R.id.dashboard_fragment_holder, new dashboard_fragment());
        dashboardTrans.commit();

        //getting Profile Picture
        userRef.child(userEmailConverted).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ProfilePurl = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();
                String NameStr = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                Glide.with(getApplicationContext())
                        .load(ProfilePurl)
                        .error(R.drawable.nalanda_bed_logo)
                        .into(userProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //getting img from camera
        userProfile.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
            LayoutInflater layoutInflater= getLayoutInflater();
            View pickImgview = layoutInflater.inflate(R.layout.image_picker_item,null);
            builder.setCancelable(true);
            builder.setView(pickImgview);
            AlertDialog alertDialogImg = builder.create();
            Window window = alertDialogImg.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            alertDialogImg.show();
            window.setAttributes(wlp);

            CardView cameraCardView = pickImgview.findViewById(R.id.chooseCamera);
            CardView galleryCardView = pickImgview.findViewById(R.id.chooseGallery);

            galleryCardView.setOnClickListener(view1 -> {
                    ImagePicker.with(this)
                            .galleryOnly()
                            .crop(1f,1f)
                            .maxResultSize(720, 1080)
                            .start(0);
                    alertDialogImg.dismiss();
            });
            cameraCardView.setOnClickListener(view1 -> {
                ImagePicker.with(this)
                        .cameraOnly()
                        .crop(1f,1f)
                        .maxResultSize(720, 1080)
                        .start(1);
                alertDialogImg.dismiss();
            });
        });

        //bottom navigation implementation

        bottomNavBar.addBubbleListener(i -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Log.d("uhj", "onBubbleClick: " + i);
            switch (i) {
                case 2131362059:
                    transaction.replace(R.id.dashboard_fragment_holder, new food_fragment());
                    break;
                case 2131362274:
                    transaction.replace(R.id.dashboard_fragment_holder, new my_room_fragment());
                    break;
                case 2131361959:
                    transaction.replace(R.id.dashboard_fragment_holder, new dashboard_fragment());
                    break;
                case 2131362017:
                    transaction.replace(R.id.dashboard_fragment_holder, new explore_fragment());
                    break;
                case 2131362252:
                    transaction.replace(R.id.dashboard_fragment_holder, new refer_fragment());
                    break;
            }
            transaction.commit();
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            profileUri = data.getData();
            userProfile.setImageURI(profileUri);
            uploadImageToFirebase(profileUri,storageRef,userRef.child(userEmailConverted));
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            profileUri = data.getData();
            userProfile.setImageURI(profileUri);
            uploadImageToFirebase(profileUri,storageRef,userRef.child(userEmailConverted));
        }
    }

    //Uploading Image to FirebaseFunction
    public static void uploadImageToFirebase(Uri ImageUri, StorageReference storageRef, DatabaseReference rdbRef){
        storageRef.putFile(ImageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                HashMap<String,Object> userMap = new HashMap<>();
                userMap.put("purl",uri.toString());
                rdbRef.updateChildren(userMap);
            });
        });

    }


}