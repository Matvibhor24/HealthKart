package com.example.healthkart_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DoctorHomeScreen extends AppCompatActivity {

    private Button editProfileButton;
    private String doctorId, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home_screen);

        editProfileButton = findViewById(R.id.edit_profile_button);

        // Retrieve doctorId and token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        doctorId = sharedPreferences.getString("doctorId", null);
        token = sharedPreferences.getString("token", null);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomeScreen.this, EditDoctorProfileScreen.class);
                intent.putExtra("DOCTOR_ID", doctorId);
                intent.putExtra("TOKEN_KEY", token);
                intent.putExtra("IS_EDIT", true);
                startActivity(intent);
            }
        });
    }
}
