package com.mcssoftware.app.mcsclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class PickTimeActivity extends AppCompatActivity {


    private TimePicker timePicker1;
    private TextView time;
    private Calendar calendar;
    private String format = "";

    private SharedPreferences sp_tripInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_time);

        timePicker1 =  findViewById(R.id.timePicker1);
       // time =  findViewById(R.id.textView1);
        calendar = Calendar.getInstance();

        sp_tripInfo = getSharedPreferences(getString(R.string.tripInfo),MODE_PRIVATE);



        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        showTime(hour, min);

        Button back = findViewById(R.id.backTime);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PickTimeActivity.this, NewActivity.class));
            }
        });
    }



    public void setTime(View view) {
        int hour = timePicker1.getCurrentHour();
        int min = timePicker1.getCurrentMinute();
        showTime(hour, min);
        startActivity(new Intent(PickTimeActivity.this, PickDateActivity.class));

    }

    public void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

//        time.setText();


        sp_tripInfo.edit().putString(getString(R.string.time), String.valueOf(new StringBuilder().append(hour).append(" : ").append(min)
               .append(" ").append(format))).apply();
    }

}
