package com.shelly.ambar.chatup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {



    private FirebaseAuth mAuth;




    // UI references.
    private  JSONObject Person_Data_Object;
    private ProgressDialog mDialog;
    private AutoCompleteTextView InputEmail;
    private AutoCompleteTextView InputPass;
    private CircleImageView circleImageView;
    private boolean ENTER_SIGN=false;
    private TextView btn_forgot_pass;
    private TextView btn_signup;
    Button mEmailSignInButton;
    LinearLayout login_activity;
    FirebaseUser user;
    private LoginButton loginButton;
    private SignInButton signInButton_google;
    private StorageReference storageReference;
    private StorageReference mStorageRef;
    private DatabaseReference UserDataBase;
    CallbackManager mCallbackManager;
    URL Profile_picture;
    GoogleApiClient googleApiClient;
    private final static int RC_SIGN_IN=9001;
    private static String TAG= "FacebookLogin";
    private static final String EMAIL = "email";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        InputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        InputPass = (AutoCompleteTextView) findViewById(R.id.password);

        circleImageView=findViewById(R.id.imageView);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        btn_forgot_pass=findViewById(R.id.btn_forgot_password);
        btn_signup=findViewById(R.id.btn_layout_signup);
        login_activity=findViewById(R.id.login_activity);
        loginButton=findViewById(R.id.login_button);





        mAuth = FirebaseAuth.getInstance();
        //set permissions

        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        storageReference=FirebaseStorage.getInstance().getReference();
        mCallbackManager = CallbackManager.Factory.create();


        //signIn
        user = mAuth.getCurrentUser();
        AccessToken accessToken=AccessToken.getCurrentAccessToken();
        if (mAuth != null || accessToken!=null) {

            mAuth.signOut();
            LoginManager.getInstance().logOut();

        }



        mEmailSignInButton.setOnClickListener(this);

        btn_forgot_pass.setOnClickListener(this);

        btn_signup.setOnClickListener(this);





        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mDialog= new ProgressDialog(LoginActivity.this);
                mDialog.setMessage("Retrieving Data..");
                mDialog.show();
                String AccessToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {



                        Person_Data_Object=object;
                        //getData(Person_Data_Object);

                    }
                });

                Bundle Parameters = new Bundle();
                Parameters.putString("fields","email");
                request.setParameters(Parameters);
                request.executeAsync();



                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                Toast.makeText(LoginActivity.this,"Canceled entering..",Toast.LENGTH_LONG).show();
                // ...
            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(LoginActivity.this,"Eror occurred,sorry. Try again later =] " +error.getMessage(),Toast.LENGTH_LONG).show();
                // ...
            }
        });












    }








    private void handleFacebookAccessToken(final AccessToken token) {


        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            mDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Entered with Success ",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            Intent intent=new Intent(LoginActivity.this,FindFriendsActivity.class);
                            intent.putExtra("publisherId", mAuth.getCurrentUser().getUid());
                            startActivity(intent);








                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed."+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"facebook login "+task.getException());
                            user=null;
                            updateUI(user);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        AccessToken accessToken=AccessToken.getCurrentAccessToken();
        if (mAuth != null || accessToken!=null) {

            mAuth.signOut();
            LoginManager.getInstance().logOut();

        }



    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser== null){
            ENTER_SIGN=false;
        }else{
            // getData(currentUser);
            ENTER_SIGN=true;
            //entering to app
            //startActivity(new Intent(LoginActivity.this,MainActivity.class));


        }

    }


    private void getData(FirebaseUser firebaseUser){

        UserDataBase=FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        UserDataBase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String CurrentStatus= Objects.requireNonNull(dataSnapshot.child("UserStatus").getValue()).toString();
                String BirthDay =Objects.requireNonNull(dataSnapshot.child("BirthDay").getValue()).toString();
                String ProfileImage= dataSnapshot.child("Thumb_Image").getValue().toString();
                String User_Name= Objects.requireNonNull(dataSnapshot.child("UserName").getValue()).toString();

                //UserNameTXT.setText("    WELCOME    "+ User_Name);

                // BirthDayText.setText("Your birthday is :"+BirthDay );
                //FriendsText.setText("Your Total Number Of Friends is:" + Friends);
                //Picasso.get().load(ProfileImage.toString()).into(ImageAvatar);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_forgot_password:

                startActivity(new Intent(LoginActivity.this, ResetPassword.class));

                break;

            case R.id.btn_layout_signup:

                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                break;

            case R.id.email_sign_in_button:

                if( InputEmail.getText().toString().equals("") || InputPass.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "One or more detail is blank.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }else{
                    loginUser(InputEmail.getText().toString(), InputPass.getText().toString());

                }
                break;



        }


    }

    private void loginUser(final String Email, final String Pass) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            Toast.makeText(LoginActivity.this,"Entered with Success ",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            Intent intent=new Intent(LoginActivity.this,FindFriendsActivity.class);
                            intent.putExtra("publisherId", mAuth.getCurrentUser().getUid());
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            // updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.

                            if (Pass.length()<6 ){
                                Snackbar snackbar= Snackbar.make(login_activity,"Password length must be over 6!",Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                            if(!Email.contains("@")){
                                Snackbar snackbar= Snackbar.make(login_activity,"Email must contain @ ",Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }

                            Toast.makeText(LoginActivity.this, "Authentication failed."+ task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
