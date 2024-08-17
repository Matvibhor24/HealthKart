package com.example.healthkart_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private TextView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        String url = "https://healthkart.onrender.com/api/auth/login";
        JSONObject loginDetails = new JSONObject();

        try {
            loginDetails.put("email", email);
            loginDetails.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, loginDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                String token = response.getString("token");
                                checkDoctorExists(token);
                            } else {
                                Toast.makeText(LoginScreen.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginScreen.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void checkDoctorExists(String token) {
        String url = "https://healthkart.onrender.com/api/doctors/exists";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject doctor = response.getJSONObject("doctorInfo");
                                String doctorId = doctor.getString("_id");

                                // Store the doctor ID in SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("doctorId", doctorId);
                                editor.putString("token", token);
                                editor.apply();

                                // Redirect to DoctorHomeScreen
                                Intent intent = new Intent(LoginScreen.this, DoctorHomeScreen.class);
                                intent.putExtra("DOCTOR_ID", doctorId);
                                intent.putExtra("TOKEN_KEY", token);
                                startActivity(intent);
                                finish();

                            } else {
                                // If the doctor profile does not exist, redirect to EditDoctorProfileScreen
                                Intent intent = new Intent(LoginScreen.this, EditDoctorProfileScreen.class);
                                intent.putExtra("TOKEN_KEY", token);
                                intent.putExtra("IS_EDIT", false);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginScreen.this, "Error checking doctor profile", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization",token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
}
