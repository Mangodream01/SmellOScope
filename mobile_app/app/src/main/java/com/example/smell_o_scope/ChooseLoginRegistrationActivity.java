package com.example.smell_o_scope;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);

        Button sLogin = findViewById(R.id.login);
        Button sRegistration = findViewById(R.id.registration);

        sLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplication(),LoginActivity.class);
                startActivity(intent);
                return;
            }
        });

        sRegistration.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplication(),RegistrationActivity.class);
                startActivity(intent);
                return;
            }
        });

    }
}
