package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class paymentActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,refPaid;
    String currentUserId;

    private EditText card,secuirity,date;
    private TextView details;
    private Button confirm;
    String total_bill,num_of_item;


    private RecyclerView mRecyclerView;
    private CartRecyclerAdapter mAdapter;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef,refUser;
    private ValueEventListener mDBListener;
    private List<product> mProducts;
    private List<String> allprice;
    Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mAuth= FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        currentUserId=mAuth.getCurrentUser().getUid();
        ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(currentUserId);
        refUser= FirebaseDatabase.getInstance().getReference().child("User").child(currentUserId);
        refPaid= FirebaseDatabase.getInstance().getReference().child("Paid").child(currentUserId);


        mToolbar=(Toolbar)findViewById(R.id.payment_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Payment");


        Intent i=this.getIntent();
        total_bill= i.getExtras().getString("bill");
        num_of_item= i.getExtras().getString("size");

        initializations();

        mProducts = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Cart").child(currentUserId);

        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = snapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProducts.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    product upload = productSnapshot.getValue(product.class);
                    upload.setKey(productSnapshot.getKey());
                    mProducts.add(upload);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(paymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment();
            }
        });
    }

    private void payment() {
        String cardnumber = card.getText().toString();
        String secnumber = secuirity.getText().toString();
        String exp = date.getText().toString();

        if (TextUtils.isEmpty(cardnumber) || TextUtils.isEmpty(secnumber) || TextUtils.isEmpty(exp))
        {
            Toast.makeText(paymentActivity.this,"Please complete every section...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        final HashMap<String, String> profileMap = new HashMap<>();
                        profileMap.put("card", cardnumber);
                        profileMap.put("security", secnumber);
                        profileMap.put("exp_date", exp);
                        profileMap.put("pay_date", getDateToday());
                        profileMap.put("total_bill", total_bill);
                        profileMap.put("item", num_of_item);
                        profileMap.put("phone", users.getPhone());
                        profileMap.put("address", users.getAddress());

                        for(int i=0; i<mProducts.size(); i++){
                            product p = mProducts.get(i);
                            profileMap.put("key", p.getKey());
                            profileMap.put("imageUrl", p.getImageUrl());
                            profileMap.put("name", p.getName());
                            profileMap.put("price", p.getPrice());

                            String uploadId = refPaid.push().getKey();

                            refPaid.child(uploadId).setValue(profileMap);
                        }

                        openImagesActivity();

                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(paymentActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    private void initializations() {
        card= (EditText)findViewById(R.id.et_credit_card);
        secuirity= (EditText)findViewById(R.id.et_secuirity_code);
        date= (EditText)findViewById(R.id.et_expire_date);
        details= (TextView)findViewById(R.id.tv_details_bill);
        confirm= (Button)findViewById(R.id.btn_confirm_pay);

        details.setText("Total Product : "+num_of_item+"\nTotal Bill : "+total_bill);
    }

    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        String today= dateFormat.format(date);
        return today;
    }

    private void openImagesActivity(){
        Toast.makeText(paymentActivity.this, "Payment Complete", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}