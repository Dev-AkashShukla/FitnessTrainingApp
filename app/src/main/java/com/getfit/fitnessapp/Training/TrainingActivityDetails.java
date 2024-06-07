package com.getfit.fitnessapp.Training;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.getfit.fitnessapp.R;

import pl.droidsonroids.gif.GifImageView;

public class TrainingActivityDetails extends AppCompatActivity {

    String buttonValue;
    Button startBtn;
    private CountDownTimer countDownTimer;
    TextView mTextView, textViewHow;
    GifImageView gifImageView;
    private boolean mTimeRunning;
    private long mTimeLeftInMills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_details);

        Intent intent = getIntent();
        buttonValue = intent.getStringExtra("value");

        int intValue = Integer.valueOf(buttonValue);

        switch (intValue) {

            case 1:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose1));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_1);
                break;
            case 2:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose2));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_2);
                break;
            case 3:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose3));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_3);
                break;
            case 4:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose4));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_4);
                break;
            case 5:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose5));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_5);
                break;
            case 6:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose6));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_6);
                break;
            case 7:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose7));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_7);
                break;
            case 8:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose8));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_8);
                break;
            case 9:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose9));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_9);
                break;
            case 10:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose10));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_10);
                break;
            case 11:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose11));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_11);
                break;
            case 12:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose12));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_12);
                break;
            case 13:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose13));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_13);
                break;
            case 14:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose14));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_14);
                break;
            case 15:
                setContentView(R.layout.activity_exercise);
                textViewHow = findViewById(R.id.textViewHow);
                textViewHow.setText(getResources().getString(R.string.pose15));
                gifImageView = findViewById(R.id.gifImage);
                gifImageView.setImageResource(R.drawable.exersice_15);
                break;

        }

        startBtn = findViewById(R.id.startButtonActivity);
        mTextView = findViewById(R.id.timeActivity);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTimeRunning) {

                    stopTimer();

                } else {

                    startTimer();

                }

            }
        });

    }

    private void stopTimer() {

        countDownTimer.cancel();
        mTimeRunning = false;
        startBtn.setText("START");

    }

    private void startTimer() {

        final CharSequence first_value = mTextView.getText();
        String first_num = first_value.toString();
        String second_num = first_num.substring(0, 2);
        String third_num = first_num.substring(3, 5);

        final int number = Integer.valueOf(second_num) * 60 + Integer.valueOf(third_num);
        mTimeLeftInMills = number * 1000;

        countDownTimer = new CountDownTimer(mTimeLeftInMills, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mTimeLeftInMills = millisUntilFinished;
                updateTimer();

            }

            @Override
            public void onFinish() {

                int newValue = Integer.valueOf(buttonValue) + 1;
                if (newValue <= 7) {

                    Intent intent = new Intent(TrainingActivityDetails.this, TrainingActivityDetails.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("value", String.valueOf(newValue));
                    startActivity(intent);

                } else {

                    newValue = 1;
                    Intent intent = new Intent(TrainingActivityDetails.this, TrainingActivityDetails.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("value", String.valueOf(newValue));
                    startActivity(intent);

                }

            }
        }.start();
        startBtn.setText("Pause");
        mTimeRunning = true;

    }

    private void updateTimer() {

        int minutes = (int) mTimeLeftInMills / 60000;
        int seconds = (int) mTimeLeftInMills % 60000 / 1000;

        String timeLeftText = "";
        if (minutes < 10) {
            timeLeftText = "0";
        }
        timeLeftText = timeLeftText + minutes + ":";
        if (seconds < 10) {
            timeLeftText += "0";
        }
        timeLeftText += seconds;
        mTextView.setText(timeLeftText);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}