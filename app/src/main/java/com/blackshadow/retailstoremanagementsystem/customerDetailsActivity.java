package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class customerDetailsActivity extends AppCompatActivity {
    private TextView cName,cPhn,check;
    private Button block,unblock;
    private LinearLayout cart,paid;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private DatabaseReference refAccept,refDelete,ref,refUser;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);


        initializeFields();


        mToolbar=(Toolbar)findViewById(R.id.customer_details_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Customer profile");

        uid=getIntent().getExtras().getString("uid");

        refUser= FirebaseDatabase.getInstance().getReference().child("User");
        ref= FirebaseDatabase.getInstance().getReference().child("User").child(uid);
        refAccept= FirebaseDatabase.getInstance().getReference().child("User");
        refDelete= FirebaseDatabase.getInstance().getReference().child("User");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String n =dataSnapshot.child("name").getValue().toString();
                String p =dataSnapshot.child("phone").getValue().toString();
                cName.setText("Name : "+ n);
                cPhn.setText("Phone : "+ p);

                if(dataSnapshot.child("user_type").getValue().toString().equals("0"))
                    check.setText("Allowed");
                else
                    check.setText("Blocked");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    refAccept.child(uid).child("user_type").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(customerDetailsActivity.this, "Unblocked", Toast.LENGTH_LONG).show();

                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(customerDetailsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refDelete.child(uid).child("user_type").setValue("4").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(customerDetailsActivity.this, "blocked", Toast.LENGTH_LONG).show();

                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(customerDetailsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pIntent= new Intent(customerDetailsActivity.this,paidProductActivity.class);
                pIntent.putExtra("uid",uid);
                startActivity(pIntent);
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cIntent= new Intent(customerDetailsActivity.this,cartProductActivity.class);
                cIntent.putExtra("uid",uid);
                startActivity(cIntent);
            }
        });


    }

    private void initializeFields() {
        cName=(TextView)findViewById(R.id.tvNameofC);
        cPhn=(TextView)findViewById(R.id.tvphnOfC);
        check=(TextView)findViewById(R.id.tvCheckC);

        unblock=(Button)findViewById(R.id.acceptC);
        block=(Button)findViewById(R.id.declineC);

        paid=(LinearLayout)findViewById(R.id.btnViewPurchase);
        cart=(LinearLayout)findViewById(R.id.btnViewCart);
    }
}