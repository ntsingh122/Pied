package com.example.news;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;


import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button loginButton,registerButton;
    private TextInputLayout emailL,passL;
    AlertDialog.Builder alertDialoguilder;
    AlertDialog alertDialog;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (Button) findViewById(R.id.buttonSign);
        registerButton = (Button) findViewById(R.id.buttonReg);
        emailL = (TextInputLayout) findViewById(R.id.emailEdit);
        passL = (TextInputLayout) findViewById(R.id.passEdit);
        mAuth = FirebaseAuth.getInstance();
        parentLayout = findViewById(android.R.id.content);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentreg = new Intent(MainActivity.this , Signup.class);
                startActivity(intentreg);
                finish();

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginButton.getText().toString().equalsIgnoreCase("NEXT")) {
                    emailL.startAnimation(textBoxGo());
                    emailL.setVisibility(View.INVISIBLE);
                    passL.setVisibility(View.VISIBLE);
                    passL.startAnimation(textBoxCome());
                    loginButton.setText("SIGN IN");
                } else {
                    String email = emailL.getEditText().getText().toString();
                    String pass = passL.getEditText().getText().toString();


                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                        loginButton.startAnimation(shakeError());
                        String errormsg = "Please fill up the user details !";
                        Snackbar.make(parentLayout, errormsg, Snackbar.LENGTH_LONG).show();
                    } else {
                        alertDialoguilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialoguilder.setTitle("Signing In");
                        alertDialoguilder.setMessage("Please wait while we are Logging In.");
                        alertDialoguilder.create();
                        alertDialog = alertDialoguilder.show();
                        signIn(email, pass);
                    }

                }
            }
        });



    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        alertDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("loginmsg", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this , Feed.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();                            //s/tartActivity(intentlog);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("loginmsg", "signInWithEmail:failure", task.getException());
                           String errormsg=  task.getException().getMessage();
                            loginButton.startAnimation(shakeError());
                            Toast.makeText(MainActivity.this,errormsg , Toast.LENGTH_SHORT).show();
                            Snackbar.make(parentLayout,"Authentication failed.",Snackbar.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
//        {   View parentLayout = findViewById(android.R.id.content);
//            Snackbar.make(parentLayout, "User is there",Snackbar.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this,Feed.class);
            startActivity(intent);
            finish();
        }


    }

    public TranslateAnimation shakeError(){
        TranslateAnimation shake = new TranslateAnimation(0,10,0,0);
        shake.setDuration(500);
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }
 public TranslateAnimation textBoxGo(){
        TranslateAnimation shake = new TranslateAnimation(0,-1*getScreenWidth(),0,0);
        shake.setDuration(500);
        return shake;
    }public TranslateAnimation textBoxCome(){
        TranslateAnimation shake = new TranslateAnimation(getScreenWidth(),0,0,0);
        shake.setDuration(500);
        return shake;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
