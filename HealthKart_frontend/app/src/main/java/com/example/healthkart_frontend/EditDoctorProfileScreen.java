package com.example.healthkart_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
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

public class EditDoctorProfileScreen extends AppCompatActivity {

    private EditText nameEditText, contactEditText, addressEditText;
    private Spinner specializationSpinner;
    private GridLayout availableTimingsGrid;
    private Button saveButton;
    private boolean isEdit = false;  // Flag to check if editing existing profile
    private String token;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile_screen);

        // Initialize views
        nameEditText = findViewById(R.id.edit_name);
        contactEditText = findViewById(R.id.edit_contact_number);
        addressEditText = findViewById(R.id.edit_address);
        specializationSpinner = findViewById(R.id.spinner_specialization);
        availableTimingsGrid = findViewById(R.id.grid_available_timings);
        saveButton = findViewById(R.id.save_button);

        // Get token from Intent or SharedPreferences
        Intent intent = getIntent();
        token = intent.getStringExtra("TOKEN_KEY");

        // Check if this is for editing or new profile creation
        isEdit = intent.getBooleanExtra("IS_EDIT", false);

        if (isEdit) {
            doctorId = intent.getStringExtra("DOCTOR_ID");
            fetchDoctorDetails();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    updateDoctorProfile();
                } else {
                    createDoctorProfile();
                }
            }
        });
    }

    private void fetchDoctorDetails() {
        String url = "https://healthkart.onrender.com/api/doctors/exists";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject doctor = response.getJSONArray("doctorInfo").getJSONObject(0);
                                doctorId = doctor.getString("_id");
                                // Populate fields with the existing doctor details
                                nameEditText.setText(doctor.getString("name"));
                                contactEditText.setText(doctor.getString("contact_number"));
                                addressEditText.setText(doctor.getString("address"));
                                // Populate specialization spinner and available timings
                                // ...

                            } else {
                                Toast.makeText(EditDoctorProfileScreen.this, "No profile found.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditDoctorProfileScreen.this, "Failed to load doctor details.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void createDoctorProfile() {
        String url = "https://healthkart.onrender.com/api/doctors/add";
        JSONObject doctorDetails = new JSONObject();

        try {
            doctorDetails.put("name", nameEditText.getText().toString());
            doctorDetails.put("speciality", specializationSpinner.getSelectedItem().toString());
            doctorDetails.put("address", addressEditText.getText().toString());

            // Add available timings as a JSONArray
            JSONArray timings = new JSONArray();
            for (int i = 0; i < availableTimingsGrid.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) availableTimingsGrid.getChildAt(i);
                if (checkBox.isChecked()) {
                    timings.put(checkBox.getText().toString());
                }
            }
            doctorDetails.put("timings", timings);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, doctorDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(EditDoctorProfileScreen.this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
                                // Navigate to DoctorHomeScreen or any other screen
                            } else {
                                Toast.makeText(EditDoctorProfileScreen.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditDoctorProfileScreen.this, "Failed to create profile.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void updateDoctorProfile() {
        String url = "https://healthkart.onrender.com/api/doctors/" + doctorId;
        JSONObject doctorDetails = new JSONObject();

        try {
            doctorDetails.put("name", nameEditText.getText().toString());
            doctorDetails.put("speciality", specializationSpinner.getSelectedItem().toString());
            doctorDetails.put("address", addressEditText.getText().toString());

            // Add available timings as a JSONArray
            JSONArray timings = new JSONArray();
            for (int i = 0; i < availableTimingsGrid.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) availableTimingsGrid.getChildAt(i);
                if (checkBox.isChecked()) {
                    timings.put(checkBox.getText().toString());
                }
            }
            doctorDetails.put("timings", timings);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, doctorDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(EditDoctorProfileScreen.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                // Navigate to DoctorHomeScreen or any other screen
                            } else {
                                Toast.makeText(EditDoctorProfileScreen.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditDoctorProfileScreen.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
}
