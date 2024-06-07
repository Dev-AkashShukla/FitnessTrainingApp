package com.getfit.fitnessapp.SignInUpProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getfit.fitnessapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfileActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private TextView textViewAuthenticated;
    private ProgressBar progressBar;
    private String userPwd;
    private Button buttonReAuthenticate, buttonDeleteUser;

    private SwipeRefreshLayout swipeContainer;

    private static final String TAG = "DeleteProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        getSupportActionBar().setTitle("Delete Your Profile");

        swipeToRefresh();

        progressBar = findViewById(R.id.progressBar);
        editTextUserPwd = findViewById(R.id.editText_delete_user_pwd);
        textViewAuthenticated = findViewById(R.id.textView_delete_user_authenticated);
        buttonDeleteUser = findViewById(R.id.button_delete_user);
        buttonReAuthenticate = findViewById(R.id.button_delete_user_authenticate);

        // Disable Delete User Button until User is authenticated
        buttonDeleteUser.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(DeleteProfileActivity.this, "Something went wrong! " +
                    "User details are not available at the moment.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void swipeToRefresh() {
        // Look up for the Swipe Container
        swipeContainer = findViewById(R.id.swipeContainer);

        // Setup Refresh Listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Code to refresh goes here. Make sure to call swipeContainer.setRefresh(false) once the refresh is complete
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
            swipeContainer.setRefreshing(false);
        });

        // Configure refresh colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    // ReAuthenticate User before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(v -> {
            userPwd = editTextUserPwd.getText().toString();

            if (TextUtils.isEmpty(userPwd)) {
                Toast.makeText(DeleteProfileActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                editTextUserPwd.setError("Please enter your current password to authenticate");
                editTextUserPwd.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                // ReAuthenticate User now
                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Disable editText for Password
                        editTextUserPwd.setEnabled(false);

                        // Enable Delete User Button. Disable Authenticate Button
                        buttonReAuthenticate.setEnabled(false);
                        buttonDeleteUser.setEnabled(true);

                        // Set TextView to show User is authenticated/verified
                        textViewAuthenticated.setText("You are authenticated/verified. You can delete your profile and related data now!");
                        Toast.makeText(DeleteProfileActivity.this, "Password has been verified. " +
                                "You can delete your profile now", Toast.LENGTH_SHORT).show();

                        // Update color of Delete User Button
                        buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(
                                DeleteProfileActivity.this, R.color.dark_green));

                        buttonDeleteUser.setOnClickListener(v1 -> showAlertDialog());
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showAlertDialog() {
        // Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete User and Related Data?");
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible!");

        // Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", (dialog, which) -> deleteUserData(firebaseUser));

        // Return to User Profile Activity if User presses Cancel Button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Change the button color of Continue
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red)));

        // Show the AlertDialog
        alertDialog.show();
    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                authProfile.signOut();
                Toast.makeText(DeleteProfileActivity.this, "User has been deleted!", Toast.LENGTH_SHORT).show();
                // Open Google Form URL in a web browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/VxMEYLkmCgarF3SA7"));
                startActivity(intent);

            } else {
                try {
                    throw task.getException();
                } catch (Exception e) {
                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }


    // Delete all the data of User
    private void deleteUserData(FirebaseUser firebaseUser) {
        // Delete Display Pic. Also check if the user has uploaded any pic before deleting
        if (firebaseUser.getPhotoUrl() != null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(unused -> Log.d(TAG, "OnSuccess: Photo Deleted"))
                    .addOnFailureListener(e -> {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        // Delete Data from Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(unused -> {
            Log.d(TAG, "OnSuccess: User Data Deleted");

            // Finally delete the user after deleting the related data
            deleteUser();
        }).addOnFailureListener(e -> {
            Log.d(TAG, e.getMessage());
            Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
