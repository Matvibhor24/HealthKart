package com.example.healthkart_frontend;

import android.content.Intent;
import android.os.Bundle;
 import android.view.View;
 import android.widget.Button;
 import android.widget.HorizontalScrollView;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.TextView;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.cardview.widget.CardView;

public class HomeScreen extends AppCompatActivity {

    private TextView hiiTxt, howAreTxt, bookText, drSpeciality, recommendationDr;
    private Button findDoctorBtn;
    private ImageView drImage;
    private HorizontalScrollView drHrzView;
    private LinearLayout drOptionsLayout;
    private CardView mainCard, recommendationCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize the views
        hiiTxt = findViewById(R.id.hii_txt);
        howAreTxt = findViewById(R.id.howAreTxt);
        bookText = findViewById(R.id.booktext);
        findDoctorBtn = findViewById(R.id.findDoctorBtn);
        drSpeciality = findViewById(R.id.drspeciality);
        recommendationDr = findViewById(R.id.recomendationdr);
        drImage = findViewById(R.id.drImage);
        drHrzView = findViewById(R.id.drhrzview);
        drOptionsLayout = findViewById(R.id.drOptionsLayout);
        mainCard = findViewById(R.id.maincard);
        recommendationCard = findViewById(R.id.recommendationCard);

        // Set up any necessary functionality
        setUpListeners();
    }

    private void setUpListeners() {
        findDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, AllDoctors.class);
                startActivity(intent);
            }
        });
    }
}

