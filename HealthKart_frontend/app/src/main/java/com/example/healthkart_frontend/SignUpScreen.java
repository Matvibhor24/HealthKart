package com.example.healthkart_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpScreen extends AppCompatActivity {
    EditText username, email, password, confirmPassword;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        register = findViewById(R.id.registerBtn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameText = username.getText().toString().trim();
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String confirmPasswordText = confirmPassword.getText().toString().trim();

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

                registerUser(usernameText, emailText, passwordText);
            }
        });
    }

    private void registerUser(String username, String email, String password) {
        String url = "https://healthkart.onrender.com/api/register";

        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("username", username);
            jsonParam.put("email", email);
            jsonParam.put("password", password);
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
                                Toast.makeText(SignUpScreen.this, "Registration successful! Token: " + token, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpScreen.this, HomeScreen.class));
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
}
