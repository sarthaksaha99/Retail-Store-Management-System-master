package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class profileActivity extends AppCompatActivity {

    private EditText name,phone,address;
    private Button editinfo, changepass;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,reference;
    private LinearLayout profile, paybill, history, product;

    public String UserStatus,UserName,category,currentUserId,currentUserGmail,stdRoll,u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializations();

        mAuth=FirebaseAuth.getInstance();
        ref= FirebaseDatabase.getInstance().getReference();
        reference=  FirebaseDatabase.getInstance().getReference();
        currentUser=mAuth.getCurrentUser();

        mToolbar=(Toolbar)findViewById(R.id.profile_activity_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");

        currentUserId=mAuth.getCurrentUser().getUid();
        currentUserGmail=mAuth.getCurrentUser().getEmail();

        ref.child("User").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String n = dataSnapshot.child("name").getValue().toString();
                String p = dataSnapshot.child("phone").getValue().toString();
                String a = dataSnapshot.child("address").getValue().toString();
                u = dataSnapshot.child("user_type").getValue().toString();
                
                name.setText(n);
                phone.setText(p);
                address.setText(a);
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_pass();
            }
        });

        editinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_info_function();
            }
        });
    }

    private void edit_info_function() {
        final String updated_name= name.getText().toString();
        final String updated_phone= phone.getText().toString();
        final String updated_add= address.getText().toString();


        if (TextUtils.isEmpty(updated_name)|| TextUtils.isEmpty(updated_phone)|| TextUtils.isEmpty(updated_add)){
            Toast.makeText(profileActivity.this,"Set name and phone number correctly",Toast.LENGTH_SHORT).show();
        }
        else{
            final HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", updated_name);
            profileMap.put("phone", updated_phone);
            profileMap.put("address", updated_add);
            profileMap.put("user_type", u);
//                profileMap.put("access","Not Checked");

            reference.child("User").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(profileActivity.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(profileActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void change_pass() {
        AlertDialog.Builder builder= new AlertDialog.Builder(profileActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter new password");

        final EditText resetPw=new EditText(profileActivity.this);
        resetPw.setHint("password");
        builder.setView(resetPw);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ResetPwSt=resetPw.getText().toString();
                if (TextUtils.isEmpty(ResetPwSt)){
                    Toast.makeText(profileActivity.this,"Please write a Strong Password",Toast.LENGTH_SHORT).show();
                }else {
                    currentUser.updatePassword(String.valueOf(resetPw)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(profileActivity.this,"Password Updated",Toast.LENGTH_SHORT).show();
                            }else{
                                String message=task.getException().getMessage();
                                Toast.makeText(profileActivity.this,"Error Occured :"+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();    }

    private void initializations() {
        name=(EditText)findViewById(R.id.et_edit_name);
        phone=(EditText)findViewById(R.id.et_edit_phone);
        editinfo=(Button) findViewById(R.id.btn_edit_info);
        changepass=(Button)findViewById(R.id.btn_change_pass);
        address=(EditText)findViewById(R.id.et_edit_address);
    }
}