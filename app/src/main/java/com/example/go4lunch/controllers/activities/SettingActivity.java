package com.example.go4lunch.controllers.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.utils.notification.AlertReceiver;

import java.util.Calendar;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    Button mAlarmOn;
    Button mAlarmOff;

    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAlarmOff = findViewById(R.id.alarmOff);
        mAlarmOn = findViewById(R.id.alarmOn);
        alarmOn();
        alarmOff();
    }

    public void onTimeSet() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 11);
        c.set(Calendar.MINUTE, 47);
        c.set(Calendar.SECOND, 00);

        startAlarm(c);
    }


    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        Objects.requireNonNull(alarmManager).cancel(pendingIntent);
    }

    public void alarmOff() {
        mAlarmOff.setOnClickListener(v -> {

            if (mAlarmOff.isEnabled()) {
                cancelAlarm();
                mAlarmOff.setBackgroundResource(R.color.quantum_white_100);
                mAlarmOn.setBackgroundResource(R.color.colorPrimary);
                Toast.makeText(getApplicationContext(), getString(R.string.alarm_canceled), Toast.LENGTH_SHORT).show();
            } else if (!mAlarmOff.isEnabled()) {
                onTimeSet();
                mAlarmOff.setBackgroundResource(R.color.colorPrimary);
                mAlarmOn.setBackgroundResource(R.color.quantum_white_100);
                Toast.makeText(getApplicationContext(), getString(R.string.alarm_activated), Toast.LENGTH_SHORT).show();
            }
            //for save preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("alarmOff", mAlarmOff.isEnabled());
            editor.apply();
        });
    }

    public void alarmOn() {
        mAlarmOn.setOnClickListener(v -> {

            onTimeSet();
            Toast.makeText(getApplicationContext(), getString(R.string.alarm_activated), Toast.LENGTH_SHORT).show();

            if (mAlarmOn.isEnabled()) {
                mAlarmOn.setBackgroundColor(getResources().getColor(R.color.quantum_white_100));
                mAlarmOff.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else if (!mAlarmOn.isEnabled()) {
                mAlarmOn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mAlarmOff.setBackgroundColor(getResources().getColor(R.color.quantum_white_100));
            }
            //For save preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("alarmOn", mAlarmOn.isEnabled());
            editor.apply();
        });
    }
}