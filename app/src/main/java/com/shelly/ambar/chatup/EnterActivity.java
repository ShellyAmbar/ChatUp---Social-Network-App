package com.shelly.ambar.chatup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class EnterActivity extends AppCompatActivity {
    private Button login_Taxt;
    private Button signUp_Text;
    private ImageView logo_picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        login_Taxt=findViewById(R.id.Text_Login);
        signUp_Text=findViewById(R.id.Text_SignUp);
        logo_picture=findViewById(R.id.logo_picture);


        YoYo.with(Techniques.Landing)
                .duration(800)

                .playOn(logo_picture);

        login_Taxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterActivity.this, LoginActivity.class));
            }
        });

        signUp_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterActivity.this, SignUpCardsActivity.class));
            }
        });



    }
}
