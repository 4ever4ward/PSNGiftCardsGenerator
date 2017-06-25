package com.gnnsnowszerro.psngiftcardsgenerator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button getStartedButton = (Button) findViewById(R.id.button);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("last_used_time", Calendar.getInstance().getTimeInMillis());
        editor.apply();

        Calendar time = Calendar.getInstance();

        time.set(Calendar.HOUR_OF_DAY, 12);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);

        NotificationService.setServiceAlarm(getApplicationContext(), true, time);

    }
}
