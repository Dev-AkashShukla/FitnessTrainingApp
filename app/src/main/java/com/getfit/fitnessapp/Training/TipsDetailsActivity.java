package com.getfit.fitnessapp.Training;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.getfit.fitnessapp.R;

public class TipsDetailsActivity extends AppCompatActivity {

    TextView textViewTipsDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_details);

        textViewTipsDetails = findViewById(R.id.textViewTipsDetails);
        String dstory = getIntent().getStringExtra("story");
        textViewTipsDetails.setText(dstory);

    }

    public void tipsDetailsGoBack(View view) {

        Intent intent = new Intent(TipsDetailsActivity.this, TipsActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(TipsDetailsActivity.this, TipsActivity.class);
        startActivity(intent);
        finish();

    }
}