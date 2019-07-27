package com.shelly.ambar.chatup;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    LinearLayout reset_activity;
    private ImageView image;
    private Button btn_reset;
    private EditText InputEmail;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth=FirebaseAuth.getInstance();
        reset_activity=findViewById(R.id.reset_activity);
        image=findViewById(R.id.imageView);
        btn_reset=findViewById(R.id.reset_pass_button);
        InputEmail=findViewById(R.id.reset_email);

        btn_reset.setOnClickListener(this);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(ResetPassword.this,R.drawable.ic_back_ground));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.reset_pass_button:
                resetPassword(InputEmail.getText().toString());
                break;

        }
    }

    private void resetPassword(final String Email) {

        if(Email!=null){

            mAuth.sendPasswordResetEmail(Email)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Snackbar snackbar=Snackbar.make(reset_activity,"We sent a new password to your mail:" + Email,Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                            else{
                                Snackbar snackbar=Snackbar.make(reset_activity,"An error occurred while we tried to send you the new password. ",Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "Email must not be empty.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
