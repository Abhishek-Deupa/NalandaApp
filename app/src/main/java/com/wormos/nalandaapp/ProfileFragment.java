package com.wormos.nalandaapp;

import static android.app.Activity.RESULT_OK;
import static com.wormos.nalandaapp.Dashboard.storageRef;
import static com.wormos.nalandaapp.Dashboard.userEmailConverted;
import static com.wormos.nalandaapp.Dashboard.userRef;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    View view;
    //declaring views and variables
    ImageView studentProfilePictureIv;
    AppCompatButton logoutBtn;
    TextView studentProfileNameTv, studentProfileRoomNoTv, studentProfileRoomTypeTv;
    FirebaseDatabase database;
    DatabaseReference studentDatabaseReference;
    FirebaseAuth mAuth;
    String studentEmailConverted;
    RelativeLayout loadingProfileProgressDialog;
    Uri profileUri;
    ProgressBar profilePhotoUpdateProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        studentProfileNameTv = view.findViewById(R.id.student_profile_name_tv);
        studentProfileRoomNoTv = view.findViewById(R.id.student_profile_room_no_tv);
        studentProfileRoomTypeTv = view.findViewById(R.id.student_profile_room_type_tv);
        loadingProfileProgressDialog = view.findViewById(R.id.user_profile_progressBarRL);
        studentProfilePictureIv = view.findViewById(R.id.student_profile_picture_iv);
        profilePhotoUpdateProgress = view.findViewById(R.id.user_profile_photo_progressBar);
        logoutBtn = view.findViewById(R.id.profile_logout_btn);

        //get Firebase Database and Authentication reference
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        studentEmailConverted = Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("\\.", "%7");
        studentDatabaseReference = database.getReference("Students/" + studentEmailConverted);

        updateProfileWithFirebaseData(studentDatabaseReference);

        //logging out user
        logoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            Toast.makeText(getContext(), "You are logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), ExploreNalanda.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        //Changing Profile Picture
        studentProfilePictureIv.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            LayoutInflater layoutInflater = getLayoutInflater();
            View pickImgview = layoutInflater.inflate(R.layout.image_picker_item, null);
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
                        .crop(1f, 1f)
                        .maxResultSize(720, 1080)
                        .start(0);
                alertDialogImg.dismiss();
            });
            cameraCardView.setOnClickListener(view1 -> {
                ImagePicker.with(this)
                        .cameraOnly()
                        .crop(1f, 1f)
                        .maxResultSize(720, 1080)
                        .start(1);
                alertDialogImg.dismiss();
            });
        });

        return view;
    }

    //set data of user from firebase
    private void updateProfileWithFirebaseData(DatabaseReference studentDatabaseReference) {
        loadingProfileProgressDialog.setVisibility(View.VISIBLE);

        studentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getting data in strings
                String studentName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String studentRoomNo = Objects.requireNonNull(snapshot.child("room_no").getValue()).toString();
                String studentRoomType = Objects.requireNonNull(snapshot.child("room_type").getValue()).toString();
                String purl = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();

                //setting string data in text views
                studentProfileNameTv.setText(studentName);
                studentProfileRoomNoTv.setText(studentRoomNo);
                studentProfileRoomTypeTv.setText(studentRoomType);
                Glide.with(requireContext())
                        .load(purl)
                        .error(R.drawable.nalanda_bed_logo)
                        .into(studentProfilePictureIv);
                loadingProfileProgressDialog.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load your profile :(", Toast.LENGTH_SHORT).show();
                loadingProfileProgressDialog.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            profileUri = data.getData();
            Dashboard.uploadImageToFirebase(profileUri, storageRef, userRef.child(userEmailConverted), studentProfilePictureIv, profilePhotoUpdateProgress);
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            profileUri = data.getData();
            Dashboard.uploadImageToFirebase(profileUri, storageRef, userRef.child(userEmailConverted), studentProfilePictureIv, profilePhotoUpdateProgress);
        }
    }

}
