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

                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    return;
                }

                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
//        progressBar.setVisibility(View.VISIBLE);
        String url = "https://healthkart.onrender.com/api/auth/login";

        JSONObject loginParams = new JSONObject();
        try {
            loginParams.put("email", email);
            loginParams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, loginParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        progressBar.setVisibility(View.GONE);
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                String token = response.getString("token");
                                String role = response.getString("role");

                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("jwt_token", token);
                                editor.apply();

                                Toast.makeText(LoginScreen.this, token, Toast.LENGTH_SHORT).show();
                                 if (role.equals("Doctor")){
                                     checkDoctorEntry(token);
                                 }
                                 else {
                                     Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                                     startActivity(intent);
                                 }
                                finish();
                            } else {
                                Toast.makeText(LoginScreen.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginScreen.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
    private void checkDoctorEntry(String token) {
        String url = "https://healthkart.onrender.com/api/doctors/exists";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean exists = response.getBoolean("success");
                            Intent intent;
                            if (exists) {
                                intent = new Intent(LoginScreen.this, DoctorHomeScreen.class);
                            }
                            else {
                                intent = new Intent(LoginScreen.this, EditDoctorProfileScreen.class);
                            }
                            intent.putExtra("TOKEN_KEY", token);
                            intent.putExtra("IS_EDIT", exists);
                            if (exists) {
                                JSONArray doctorArray = response.getJSONArray("doctorInfo");
                                String doctorId = doctorArray.getJSONObject(0).getString("_id");
                                intent.putExtra("DOCTOR_ID", doctorId);
                            }
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = "Failed to check doctor entry.";
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    String responseBody = new String(error.networkResponse.data);
                    errorMsg += " Status Code: " + statusCode + ". Response Body: " + responseBody;
                } else {
                    errorMsg += " Network Response is null.";
                }
                Log.e("hii", errorMsg);
                Toast.makeText(LoginScreen.this, errorMsg, Toast.LENGTH_LONG).show();
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
