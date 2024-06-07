package com.getfit.fitnessapp.SignInUpProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getfit.fitnessapp.R;
public class OtpActivity extends AppCompatActivity {

    private EditText editTextOtp;
    private Button buttonVerifyOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        editTextOtp = findViewById(R.id.editText_otp);
        buttonVerifyOtp = findViewById(R.id.verify_otp);

        buttonVerifyOtp.setOnClickListener(v -> verifyOtp());
    }

    private void verifyOtp() {
        String enteredOtp = editTextOtp.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("OTP_PREF", MODE_PRIVATE);
        String savedOtp = sharedPreferences.getString("OTP", "");

        if (enteredOtp.equals(savedOtp)) {
            Toast.makeText(OtpActivity.this, "OTP Verified Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OtpActivity.this, UserProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            // Proceed to the next activity or mark the user as verified
        } else {
            Toast.makeText(OtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }
}
