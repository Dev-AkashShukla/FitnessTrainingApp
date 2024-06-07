package com.getfit.fitnessapp.Training;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.getfit.fitnessapp.R;
import com.getfit.fitnessapp.SignInUpProfile.UserProfileActivity;

public class TipsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        String[] tstory = getResources().getStringArray(R.array.title_story);
        final String[] dstory = getResources().getStringArray(R.array.details_story);

        listView = findViewById(R.id.listTips);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_list_tips,
                R.id.textViewRowListTips, tstory);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String t = dstory[position];
                Intent intent = new Intent(TipsActivity.this, TipsDetailsActivity.class);
                intent.putExtra("story", t);
                startActivity(intent);

            }
        });

    }

    public void tipsGoBack(View view) {

        Intent intent = new Intent(TipsActivity.this, UserProfileActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(TipsActivity.this, UserProfileActivity.class);
        startActivity(intent);
        finish();

    }
}