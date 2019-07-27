package com.shelly.ambar.chatup;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForgotPassActivity extends AppCompatActivity {

    private CircleImageView imageView;

    private TextView InputPass1;

    private Button Change_Password;
    private FirebaseAuth mAuth;


    private LinearLayout activity_forgot_pass;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        Change_Password=findViewById(R.id.forgot_pass_button);

        InputPass1=findViewById(R.id.password1);

        imageView=findViewById(R.id.imageView);
        activity_forgot_pass=findViewById(R.id.activity_forgot_pass);

        //toolbar
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset your password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setNavigationIcon(ContextCompat.getDrawable(ForgotPassActivity.this,R.drawable.ic_back_ground));


        mAuth=FirebaseAuth.getInstance();


        Change_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changePass(InputPass1.getText().toString());

            }
        });




    }

    public void changePass(String password){
        FirebaseUser user = mAuth.getCurrentUser();
        user.updatePassword(password).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar snackbar=Snackbar.make(activity_forgot_pass,"Password has been changed successfully",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    finish();
                }else{
                    Snackbar snackbar=Snackbar.make(activity_forgot_pass,"Error occurred while trying to change password",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    finish();

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
