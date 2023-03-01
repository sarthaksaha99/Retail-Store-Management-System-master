package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class registrationActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText userEmail, userPasswrod;
    private TextView alreadyHaveAccountLink;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();


        rootRef= FirebaseDatabase.getInstance().getReference();
        InitializeFields();

        alreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void InitializeFields() {
        createAccountButton=(Button)findViewById(R.id.register_button);
        userEmail=(EditText)findViewById(R.id.register_email);
        userPasswrod=(EditText)findViewById(R.id.register_password);
        alreadyHaveAccountLink=(TextView)findViewById(R.id.already_have_account_link);

        loadingBar= new ProgressDialog(this);
    }

    private void CreateNewAccount() {
        String email=userEmail.getText().toString();
        String password=userPasswrod.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter an email...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your Password...",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String currentUserId=mAuth.getCurrentUser().getUid();
                                rootRef.child("User").child(currentUserId).setValue("");
                                SendUserToLoginActivity();
                                Toast.makeText(registrationActivity.this,"Account created successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        });

                    }else
                    {
                        String message= task.getException().toString();
                        Toast.makeText(registrationActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent= new Intent(registrationActivity.this,loginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void SendUserToMainActivity() {
        Intent mainIntent= new Intent(registrationActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}