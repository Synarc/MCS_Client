package com.mcssoftware.app.mcsclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class PickDateActivity extends AppCompatActivity {


    DatePicker picker;
    Button btnGet, back;

    private SharedPreferences sp_tripInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_date);

        picker=(DatePicker)findViewById(R.id.datePicker1);
        btnGet=(Button)findViewById(R.id.button1);
        back = findViewById(R.id.backDate);

        sp_tripInfo = getSharedPreferences(getString(R.string.tripInfo), MODE_PRIVATE);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String dateString =  picker.getDayOfMonth()+"/"+ (picker.getMonth() + 1)+"/"+picker.getYear();

               sp_tripInfo.edit().putString(getString(R.string.date), dateString).apply();
                startActivity(new Intent(PickDateActivity.this, ReviewNewReqActivity.class));

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PickDateActivity.this, PickTimeActivity.class));
            }
        });


    }

}
