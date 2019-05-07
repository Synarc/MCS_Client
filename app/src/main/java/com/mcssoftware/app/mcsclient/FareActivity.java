package com.mcssoftware.app.mcsclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FareActivity extends AppCompatActivity {


    private SharedPreferences sp_tripIfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare);


        sp_tripIfo = getSharedPreferences(getString(R.string.tripInfo), MODE_PRIVATE);

        TextView fare = findViewById(R.id.pricefare);

        String price = Long.toString(sp_tripIfo.getLong(getString(R.string.fare), 56));

        fare.setText("K "+ price);


        Button accept, back;

        accept = findViewById(R.id.accept);
        back = findViewById(R.id.backFarePage);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FareActivity.this, PickTimeActivity.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FareActivity.this, NewActivity.class));

            }
        });
    }
}
