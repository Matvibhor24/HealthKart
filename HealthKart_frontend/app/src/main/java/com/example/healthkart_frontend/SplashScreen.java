package com.example.healthkart_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    TextView tv,tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", null);


        tv = findViewById(R.id.hellotxt);
        tv2 = findViewById(R.id.goToLogIn);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (token!=null) {
//                    Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
//                    startActivity(intent);
//                    finish();
//                } else {
                    Intent intent = new Intent(SplashScreen.this, SignUpScreen.class);
                    startActivity(intent);
//                }

            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (token != null) {
//                    Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
//                    startActivity(intent);
//                    finish();
//                } else {
                    Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
                    startActivity(intent);
//                }

            }
        });
    }
}

