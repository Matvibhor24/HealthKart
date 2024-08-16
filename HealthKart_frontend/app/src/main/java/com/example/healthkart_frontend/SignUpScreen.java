package com.example.healthkart_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {
    EditText username, email, password, confirmPassword;
    TextView register;
    Spinner roleSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        register = findViewById(R.id.registerBtn);
        roleSpinner = findViewById(R.id.role_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameText = username.getText().toString().trim();
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String confirmPasswordText = confirmPassword.getText().toString().trim();
                String selectedRole = roleSpinner.getSelectedItem().toString(); // Get selected role

                if (TextUtils.isEmpty(usernameText)) {
                    username.setError("Username is required");
                    return;
                }

                if (TextUtils.isEmpty(emailText)) {
                    email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(passwordText)) {
                    password.setError("Password is required");
                    return;
                }

                if (!passwordText.equals(confirmPasswordText)) {
                    Toast.makeText(SignUpScreen.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(usernameText, emailText, passwordText, selectedRole);
            }
        });
    }

    private void registerUser(String username, String email, String password, String role) {
        String url = "https://healthkart.onrender.com/api/auth/register";
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("username", username);
            jsonParam.put("email", email);
            jsonParam.put("password", password);
            jsonParam.put("role", role); // Add role to the JSON object
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                String token = response.getString("token");
                                String role = response.getString("role");

                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("jwt_token", token);
                                editor.apply();

                                Toast.makeText(SignUpScreen.this, token, Toast.LENGTH_SHORT).show();
                                if (role.equals("Doctor")){
                                    checkDoctorEntry(token);
                                }
                                else {
                                    Intent intent = new Intent(SignUpScreen.this, HomeScreen.class);
                                    startActivity(intent);
                                }
                                finish();
                            } else {
                                Toast.makeText(SignUpScreen.this, "Registration failed: " + response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SignUpScreen.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignUpScreen.this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        int socketTimeout = 30000; // 30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(SignUpScreen.this);
        requestQueue.add(jsonObjectRequest);
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
                                intent = new Intent(SignUpScreen.this, DoctorHomeScreen.class);
                            } else {
                                intent = new Intent(SignUpScreen.this, EditDoctorProfileScreen.class);
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
                Toast.makeText(SignUpScreen.this, errorMsg, Toast.LENGTH_LONG).show();
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
