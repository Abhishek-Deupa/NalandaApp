package com.wormos.nalandaapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class SelectHostel extends AppCompatActivity {

    int seater = 0;
    String selectedHostel = null;
    TextView oneSeater, twoSeater, threeSeater, fourSeater, chanakayaHostelTV, dronaHostelTV, collegeHostelTV, chanakayaLocationTV, dronaLocationTV, collegeLocationTV, vanyaHostelTV, vanyaLocationTV;
    ImageView chanakayaHostel, dronaHostel, collegeHostel, vanyaHostel, vanyaLocationIcon;
    AppCompatButton selectDetailBackBtn;
    CardView chanakayaDetail, dronaDetail, collegeDetail, vanyaDetail, selectHostelPayBtn;
    boolean isFemale = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_hostel);

        //Initialization
        oneSeater = findViewById(R.id.select_1_seater);
        twoSeater = findViewById(R.id.select_2_seater);
        threeSeater = findViewById(R.id.select_3_seater);
        fourSeater = findViewById(R.id.select_4_seater);
        chanakayaHostel = findViewById(R.id.select_chanakaya_hostel);
        dronaHostel = findViewById(R.id.select_drona_hostel);
        collegeHostel = findViewById(R.id.select_college_hostel);
        vanyaHostel = findViewById(R.id.select_vanya_hostel);
        selectDetailBackBtn = findViewById(R.id.select_hostel_back_btn);
        chanakayaDetail = findViewById(R.id.chanakaya_info);
        dronaDetail = findViewById(R.id.drona_info);
        collegeDetail = findViewById(R.id.college_info);
        vanyaDetail = findViewById(R.id.vanya_info);
        selectHostelPayBtn = findViewById(R.id.select_hostel_pay_btn);
        chanakayaHostelTV = findViewById(R.id.select_chanakaya_hostel_text);
        dronaHostelTV = findViewById(R.id.select_drona_hostel_text);
        collegeHostelTV = findViewById(R.id.select_college_hostel_text);
        vanyaHostelTV = findViewById(R.id.select_vanya_hostel_text);
        vanyaLocationTV = findViewById(R.id.select_vanya_hostel_location);
        vanyaLocationIcon = findViewById(R.id.select_vanya_hostel_icon);
        chanakayaLocationTV = findViewById(R.id.select_chanakaya_hostel_location);
        dronaLocationTV = findViewById(R.id.select_drona_hostel_location);
        collegeLocationTV = findViewById(R.id.select_college_hostel_location);

//Methodology

        //setting the girl hostel
        Log.d("gender", "onCreate: " + getIntent().getStringExtra("gender"));
        if (getIntent().getStringExtra("gender").equals("Female")) {
            femaleHostel();
            isFemale = true;
        }


        selectHostelPayBtn.setOnClickListener(view -> {
            if (selectedHostel == null) {
                Toast.makeText(this, "Select a hostel", Toast.LENGTH_SHORT).show();
            } else if (seater == 0) {
                Toast.makeText(this, "Select room capacity", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, PaymentOptions.class));
            }
        });

        selectDetailBackBtn.setOnClickListener(view -> finish());

        oneSeater.setOnClickListener(view -> {
            roomSeatSelector(oneSeater, twoSeater, threeSeater, fourSeater);
            seater = 1;
        });
        twoSeater.setOnClickListener(view -> {
            roomSeatSelector(twoSeater, oneSeater, threeSeater, fourSeater);
            seater = 2;
        });
        threeSeater.setOnClickListener(view -> {
            roomSeatSelector(threeSeater, oneSeater, twoSeater, fourSeater);
            seater = 3;
        });
        fourSeater.setOnClickListener(view -> {
            roomSeatSelector(fourSeater, oneSeater, twoSeater, threeSeater);
            seater = 4;
        });

        chanakayaHostel.setOnClickListener(view -> {
            HostelSelector(chanakayaHostel, dronaHostel, collegeHostel, vanyaHostel);
            selectedHostel = chanakayaHostelTV.getText().toString();
        });

        dronaHostel.setOnClickListener(view -> {
            HostelSelector(dronaHostel, chanakayaHostel, collegeHostel, vanyaHostel);
            selectedHostel = dronaHostelTV.getText().toString();
        });

        collegeHostel.setOnClickListener(view -> {
            HostelSelector(collegeHostel, chanakayaHostel, dronaHostel, vanyaHostel);
            selectedHostel = collegeHostelTV.getText().toString();
        });

        vanyaHostel.setOnClickListener(view -> {
            HostelSelector(vanyaHostel, collegeHostel, chanakayaHostel, dronaHostel);
            selectedHostel = vanyaHostelTV.getText().toString();
        });

        collegeDetail.setOnClickListener(view ->
                startActivity(new Intent(this, HostelDetail.class).putExtra("HostelName", collegeHostelTV.getText().toString()))
        );

        chanakayaDetail.setOnClickListener(view ->
                startActivity(new Intent(this, HostelDetail.class).putExtra("HostelName", chanakayaHostelTV.getText().toString()))
        );

        dronaDetail.setOnClickListener(view ->
                startActivity(new Intent(this, HostelDetail.class).putExtra("HostelName", dronaHostelTV.getText().toString()))
        );

        vanyaDetail.setOnClickListener(view ->
                startActivity(new Intent(this, HostelDetail.class).putExtra("HostelName", vanyaHostelTV.getText().toString()))
        );

    }


    //No of seat Selection
    public void roomSeatSelector(TextView selected, TextView other1, TextView other2, TextView other3) {
        selected.setBackground(ContextCompat.getDrawable(SelectHostel.this, R.drawable.button_style_curve));
        other1.setBackgroundColor(Color.parseColor("#f5f5f5"));
        other2.setBackgroundColor(Color.parseColor("#f5f5f5"));
        other3.setBackgroundColor(Color.parseColor("#f5f5f5"));

    }

    //Different Type of Hostel Selection
    public void HostelSelector(ImageView selected, ImageView other1, ImageView other2, ImageView other3) {
        selected.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SelectHostel.this, R.color.seat_no_background_tint)));
        selected.setBackground(ContextCompat.getDrawable(SelectHostel.this, R.drawable.button_style_more_curve));
        other1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SelectHostel.this, R.color.seat_no_white_background)));
        other2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SelectHostel.this, R.color.seat_no_white_background)));
        other3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(SelectHostel.this, R.color.seat_no_white_background)));

    }

    //after successful payment send data to database
    public void regComplete(Intent intent, DatabaseReference registrationRef) {
        HashMap<String, Object> regData = new HashMap<>();
        regData.put("name", intent.getStringExtra("name"));
        regData.put("email", intent.getStringExtra("email"));
        regData.put("phoneNo", intent.getStringExtra("phoneNo"));
        regData.put("address", intent.getStringExtra("address"));
        regData.put("university", intent.getStringExtra("university"));
        regData.put("gender", intent.getStringExtra("gender"));
        regData.put("state", intent.getStringExtra("state"));
        regData.put("city", intent.getStringExtra("city"));
        regData.put("hostel", selectedHostel);
        regData.put("seater", seater);

        registrationRef.updateChildren(regData).addOnSuccessListener(s -> {
            Toast.makeText(SelectHostel.this, "Registration successful", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(fail -> {
            Toast.makeText(SelectHostel.this, "Please try again", Toast.LENGTH_SHORT).show();
        });

    }

    //convert male hostel into female hostel if selected gender is female
    public void femaleHostel() {
        String gargi = "Gargi";
        String queens = "Queens";
        collegeHostelTV.setText(gargi);
        dronaHostelTV.setText(queens);
        vanyaHostel.setVisibility(View.GONE);
        vanyaDetail.setVisibility(View.GONE);
        vanyaLocationTV.setVisibility(View.GONE);
        vanyaHostelTV.setVisibility(View.GONE);
        vanyaLocationIcon.setVisibility(View.GONE);
    }
}