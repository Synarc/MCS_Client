package com.mcssoftware.app.mcsclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText firstName, lastName;

    Button register;
    FirebaseAuth auth;

    DatabaseReference databaseRegCLients;
    DatabaseReference databaseClientInfoInTripRequest;
    private RadioButton gender;
    private RadioGroup group;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



         user = FirebaseAuth.getInstance().getCurrentUser();
        firstName = findViewById(R.id.firstNameReg);
        lastName = findViewById(R.id.lastNameReg);

        databaseRegCLients = FirebaseDatabase.getInstance().getReference(getString(R.string.RegClient));

        databaseClientInfoInTripRequest = FirebaseDatabase.getInstance().getReference(getString(R.string.reqTrip));

        register = findViewById(R.id.registerBtn);




        register.setEnabled(true
        );

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                group = findViewById(R.id.radioBut);

                int selectId = group.getCheckedRadioButtonId();

                gender = findViewById(selectId);
                Toast.makeText(RegisterActivity.this, gender.getText().toString()+","+ firstName.getText().toString() +','+lastName.getText().toString(), Toast.LENGTH_SHORT).show();

                ClientInfo clientInfo = new ClientInfo(firstName.getText().toString(),
                        lastName.getText().toString(),
                        firstName.getText().toString() +' '+lastName.getText().toString(),
                        user.getPhoneNumber(),
                        gender.getText().toString()


                );


                databaseClientInfoInTripRequest.child(user.getUid()).child(getString(R.string.clientInfo)).setValue(clientInfo);
                databaseRegCLients.child(user.getUid()).setValue(clientInfo);

                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });







    }
}
