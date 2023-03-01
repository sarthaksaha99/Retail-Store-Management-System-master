package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class purchasedProductDetailsActivity extends AppCompatActivity {
    TextView nameDetailTextView,descriptionDetailTextView,paymentHistory;
    ImageView productDetailImageView;
    private String link,uid;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,refAdminAccess;

    final HashMap<String, String> profileMap = new HashMap<>();

    private void initializeWidgets(){
        Intent i=this.getIntent();
        link=i.getExtras().getString("key");
        uid=i.getExtras().getString("uid");
        ref= FirebaseDatabase.getInstance().getReference().child("Paid").child(uid).child(link);

        nameDetailTextView= (TextView)findViewById(R.id.name_ppd);
        descriptionDetailTextView= (TextView)findViewById(R.id.description_ppd);
        paymentHistory= (TextView)findViewById(R.id.payment_info);
        productDetailImageView=(ImageView)findViewById(R.id.ppd_activity_toolbarImageView);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_product_details);

        mToolbar=(Toolbar)findViewById(R.id.ppd_activity_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Purchase History");
        initializeWidgets();


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileMap.put("name",dataSnapshot.child("name").getValue().toString());
                profileMap.put("card",dataSnapshot.child("card").getValue().toString());
                profileMap.put("exp_date",dataSnapshot.child("exp_date").getValue().toString());
                profileMap.put("imageUrl",dataSnapshot.child("imageUrl").getValue().toString());
                profileMap.put("pay_date",dataSnapshot.child("pay_date").getValue().toString());
                profileMap.put("price",dataSnapshot.child("price").getValue().toString());
                profileMap.put("security",dataSnapshot.child("security").getValue().toString());
                profileMap.put("total_bill",dataSnapshot.child("total_bill").getValue().toString());
                profileMap.put("item",dataSnapshot.child("item").getValue().toString());
                profileMap.put("key",dataSnapshot.child("key").getValue().toString());
                profileMap.put("address",dataSnapshot.child("address").getValue().toString());
                profileMap.put("phone",dataSnapshot.child("phone").getValue().toString());

                nameDetailTextView.setText(profileMap.get("name"));
                descriptionDetailTextView.setText("Price : "+profileMap.get("price"));
                Picasso.get()
                        .load(profileMap.get("imageUrl"))
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .centerCrop()
                        .into(productDetailImageView);


                paymentHistory.setText("Credit Card Number : "+profileMap.get("card")+"\nSecurity Code : "+profileMap.get("security")+
                        "\nExpire Date : "+profileMap.get("exp_date")+"\nPayment Date : "+profileMap.get("pay_date")
                        +"\nPhone : "+profileMap.get("phone")+"\nAddress : "+profileMap.get("address"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}