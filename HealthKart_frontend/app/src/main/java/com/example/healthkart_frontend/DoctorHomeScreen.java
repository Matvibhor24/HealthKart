package com.example.healthkart_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DoctorHomeScreen extends AppCompatActivity {
    private Button editProfile;
    private String token;
    private String doctorId;
   boolean isEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_home_screen);
        editProfile = findViewById(R.id.edit_profile_button);

        Intent intent = getIntent();
        token = intent.getStringExtra("TOKEN_KEY");
        isEdit = intent.getBooleanExtra("IS_EDIT", false);
        doctorId = intent.getStringExtra("DOCTOR_ID");
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomeScreen.this, EditDoctorProfileScreen.class);
                intent.putExtra("TOKEN_KEY", token);
                intent.putExtra("IS_EDIT", isEdit);
                intent.putExtra("DOCTOR_ID", doctorId);
                startActivity(intent);
            }
        });

    }
}