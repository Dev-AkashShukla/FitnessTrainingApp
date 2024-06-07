package com.getfit.fitnessapp.Training;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.getfit.fitnessapp.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class FirstTrainingActivity extends AppCompatActivity {

    int[] newArray;
    private AdView mAdView, mAdViewSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_training);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdViewSecond = findViewById(R.id.adViewSecond);
        AdRequest adRequestSecond = new AdRequest.Builder().build();
        mAdViewSecond.loadAd(adRequestSecond);

        newArray = new int[] {
                R.id.bow_pose,
                R.id.bridge_pose,
                R.id.chair_pose,
                R.id.child_pose,
                R.id.cobbler_pose,
                R.id.cow_pose,
                R.id.playji_pose,
                R.id.pauseji_pose,
                R.id.plank_pose,
                R.id.crunches_pose,
                R.id.situp_pose,
                R.id.rotation_pose,
                R.id.twist_pose,
                R.id.windmill_pose,
                R.id.legup_pose
        };

    }

    public void imageButtonClicked(View view) {

        for (int i = 0; i < newArray.length; i++) {

            if (view.getId() == newArray[i]) {

                int value = i + 1;
                Toast.makeText(FirstTrainingActivity.this, String.valueOf(value), Toast.LENGTH_SHORT).show();
                Log.i("FIRST", String.valueOf(value));

                Intent intent = new Intent(FirstTrainingActivity.this, TrainingActivityDetails.class);
                intent.putExtra("value", String.valueOf(value));
                startActivity(intent);

            }

        }

    }
}