package com.getfit.fitnessapp.SignInUpProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.getfit.fitnessapp.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB,
            editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd;

    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        // Setting up DatePicker on EditText
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(String.format("%02d-%02d-%d", dayOfMonth, month + 1, year));
                    }
                }, year, month, day);

                // Set min date to 15/08/1940
                calendar.set(1940, Calendar.AUGUST, 15);
                picker.getDatePicker().setMinDate(calendar.getTimeInMillis());

                // Set max date to 20/08/2021
                calendar.setTimeInMillis(System.currentTimeMillis());
                picker.getDatePicker().setMaxDate(calendar.getTimeInMillis());

                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                if (selectedGenderId != -1) {
                    radioButtonRegisterGenderSelected = findViewById(selectedGenderId);
                }

                // Obtain the entered data
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textGender = radioButtonRegisterGenderSelected != null ? radioButtonRegisterGenderSelected.getText().toString() : "";

                // Validate Mobile Number using Matcher and Pattern (Regular Expression)
                String mobileRegex = "[6-9][0-9]{9}"; // First no. can be {6,8,9} and rest 9 nos. can be any no.
                Matcher mobileMatcher = Pattern.compile(mobileRegex).matcher(textMobile);

                if (TextUtils.isEmpty(textFullName)) {
                    showError(editTextRegisterFullName, "Please enter your full name");
                } else if (TextUtils.isEmpty(textEmail)) {
                    showError(editTextRegisterEmail, "Please enter your email");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    showError(editTextRegisterEmail, "Please re-enter your email");
                } else if (TextUtils.isEmpty(textDoB)) {
                    showError(editTextRegisterDoB, "Please enter your date of birth");
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    showToast("Please select your gender");
                } else if (TextUtils.isEmpty(textMobile)) {
                    showError(editTextRegisterMobile, "Please enter your mobile no.");
                } else if (textMobile.length() != 10 || !mobileMatcher.find()) {
                    showError(editTextRegisterMobile, "Please re-enter a valid mobile no.");
                } else if (TextUtils.isEmpty(textPwd)) {
                    showError(editTextRegisterPwd, "Please enter your password");
                } else if (textPwd.length() < 6) {
                    showError(editTextRegisterPwd, "Password should be at least 6 digits");
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    showError(editTextRegisterConfirmPwd, "Please confirm your password");
                } else if (!textPwd.equals(textConfirmPwd)) {
                    showError(editTextRegisterConfirmPwd, "Passwords do not match");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPwd);
                }
            }
        });
    }

    private void showError(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        editText.requestFocus();
    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            // Send verification email
                            sendVerificationEmail(firebaseUser);

                            // Update Display Name of User
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDoB, textGender, textMobile);

                            // Extracting User reference from Database for "Registered Users"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                            // Create a node for the user in the Realtime Database
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        showEmailVerificationDialog();
                                          } else {
                                        showToast("User registration failed. Please try again.");
                                    }
                                }
                            });
                        } else {
                            handleRegistrationError(task);
                        }
                    }
                });
    }


    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if the user is already signed in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Check if the user's email is verified
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser updatedUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (updatedUser.isEmailVerified()) {
                        // Move user to UserProfileActivity
                        moveToUserProfileActivity();
                    }
                }
            });
        }
    }

    private void moveToUserProfileActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void showEmailVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Email Verification");
        builder.setMessage("Please verify your email to complete the registration process. A verification link has been sent to your email.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void handleRegistrationError(Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseAuthWeakPasswordException e) {
            showError(editTextRegisterPwd, "Your password is too weak. Use a mix of alphabets, numbers, and special characters.");
        } catch (FirebaseAuthInvalidCredentialsException e) {
            showError(editTextRegisterEmail, "Your email is invalid or already in use. Kindly re-enter.");
        } catch (FirebaseAuthUserCollisionException e) {
            showError(editTextRegisterEmail, "User is already registered with this email. Use another email.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            showToast(e.getMessage());
        }
        progressBar.setVisibility(View.GONE);
    }
}
