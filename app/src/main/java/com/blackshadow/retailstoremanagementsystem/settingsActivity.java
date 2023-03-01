package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class settingsActivity extends AppCompatActivity {
    public String name, phone;
    private EditText etname, etphone,etadd;
    private Button updateButton;

    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        reference=FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUserData();
            }
        });
    }

    private void uploadUserData() {
        final String setUserName=etname.getText().toString();
        final String setUserPhone=etphone.getText().toString();
        final String setUserAddress=etadd.getText().toString();

        if (TextUtils.isEmpty(setUserPhone)|| TextUtils.isEmpty(setUserName) || TextUtils.isEmpty(setUserAddress)){
            Toast.makeText(settingsActivity.this,"Please complete every section...",Toast.LENGTH_SHORT).show();
        }
        else{
            final HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
                profileMap.put("name", setUserName);
            profileMap.put("phone", setUserPhone);
            profileMap.put("address", setUserAddress);
                profileMap.put("user_type", "0");
//                profileMap.put("access","Not Checked");

                reference.child("User").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            rafSt.child("Request Student List").child(r).setValue(profileMap);
                            GoToMainActivity();
                            Toast.makeText(settingsActivity.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(settingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }


    private void InitializeFields(){
        etname= (EditText)findViewById(R.id.et_register_name);
        etphone= (EditText)findViewById(R.id.et_register_phone);
        etadd=(EditText)findViewById(R.id.et_register_Address);
        updateButton= (Button) findViewById(R.id.update_acc_button);
    }

    private void GoToMainActivity() {
        Intent mainIntent= new Intent(settingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}