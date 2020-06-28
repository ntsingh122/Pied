package com.example.news;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Signup extends AppCompatActivity {
    private Button registerButton;

    private TextInputLayout mUsername;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;

    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    private Toolbar toolbar;
    View parentLayout;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextInputLayout) findViewById(R.id.displayEmailReg);
        mPassword = (TextInputLayout) findViewById(R.id.displayPasswordReg);
        mUsername = (TextInputLayout) findViewById(R.id.displayNameReg);
        registerButton = (Button) findViewById(R.id.regCreateBtn);
        toolbar =  findViewById(R.id.regTool);
        parentLayout = findViewById(android.R.id.content);

        progressBar = (ProgressBar) findViewById(R.id.progBar);
        progressBar.setVisibility(View.INVISIBLE);

        toolbar.setTitle("Register");
        setSupportActionBar(toolbar);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Objects.requireNonNull(mUsername.getEditText()).getText().toString();
                String email = Objects.requireNonNull(mEmail.getEditText()).getText().toString();
                String password = mPassword.getEditText().getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)||TextUtils.isEmpty(username))
                {
                    String errormsg=  "Please fill up the user details.";
                    Snackbar.make(parentLayout,errormsg,Snackbar.LENGTH_LONG).show();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    createAccount(username, email, password);
                }
            }
        });


    }

    public void createAccount(final String username, final String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("testp", "createUserWithEmail:success");
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();
                            myRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name",username);
                            userMap.put("status","I love myself.");
                            userMap.put("image","default");
                            myRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(Signup.this, Feed.class);
                                        intent.putExtra("message","Registered Successfully");
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });


                           // finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("testp", "createUserWithEmail:failure"+email, task.getException());
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                          //  updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
