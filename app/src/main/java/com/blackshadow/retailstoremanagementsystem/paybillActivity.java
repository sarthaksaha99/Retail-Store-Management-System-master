package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class paybillActivity extends AppCompatActivity implements CartRecyclerAdapter.OnItemClickListener{
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,refAdminAccess;

    private RecyclerView mRecyclerView;
    private CartRecyclerAdapter mAdapter;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<product> mProducts;
    private List<String> allprice;

    private TextView tbill;
    private Button pay;
//    Intent billintent = new Intent(this, historyActivity.class);


    String currentUserId;
    int bill=0;
    int psize=0;

    private void openDetailActivity(String[] data){
        Intent intent = new Intent(this, productDetailsActivity.class);
        intent.putExtra("NAME_KEY",data[0]);
        intent.putExtra("DESCRIPTION_KEY",data[1]);
        intent.putExtra("IMAGE_KEY",data[2]);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paybill);

        mAuth=FirebaseAuth.getInstance();

        currentUser=mAuth.getCurrentUser();
        currentUserId=mAuth.getCurrentUser().getUid();
        ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(currentUserId);


        mToolbar=(Toolbar)findViewById(R.id.cart_activity_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Cart");




        pay=(Button)findViewById(R.id.btnPayBill);
        tbill=(TextView)findViewById(R.id.tvAmount);

        mRecyclerView = findViewById(R.id.cRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.cartProgressBar);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setIndeterminate(false);
                mProgressBar.setProgress(0);
            }
        }, 5000);
        mProducts = new ArrayList<>();
        mAdapter = new CartRecyclerAdapter (paybillActivity.this, mProducts);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(paybillActivity.this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Cart").child(currentUserId);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProducts.clear();

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    product upload = productSnapshot.getValue(product.class);
                    upload.setKey(productSnapshot.getKey());
//                    allprice.add(upload.getPrice());
                    mProducts.add(upload);
                }
                mAdapter.notifyDataSetChanged();

                int b=0;
                for(int i=0; i<mProducts.size(); i++){
                    b = b + Integer.valueOf(mProducts.get(i).getPrice().toString());
                }
                bill=b;
                psize=mProducts.size();
                tbill.setText("Total Bill : "+bill);

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(paybillActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent billintent = new Intent(paybillActivity.this, paymentActivity.class);
                billintent.putExtra("bill",String.valueOf(bill));
                billintent.putExtra("size",String.valueOf(psize));
                Toast.makeText(paybillActivity.this, "Toatl Bill :" + bill, Toast.LENGTH_SHORT).show();
                startActivity(billintent);
            }
        });
    }

    public void onItemClick(int position) {
        product clickedProduct=mProducts.get(position);
        String[] productData={clickedProduct.getName(),clickedProduct.getPrice(),clickedProduct.getImageUrl()};
        openDetailActivity(productData);
    }

    @Override
    public void onShowItemClick(int position) {
        product clickedProduct=mProducts.get(position);
        String[] productData={clickedProduct.getName(),clickedProduct.getPrice(),clickedProduct.getImageUrl()};
        openDetailActivity(productData);
    }

    @Override
    public void onDeleteItemClick(int position) {
        product selectedItem = mProducts.get(position);
        final String selectedKey = selectedItem.getKey();
        mDatabaseRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(paybillActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                } else {
                    String message = task.getException().toString();
                    Toast.makeText(paybillActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onButtonClick(int pos) {
        product selectedItem = mProducts.get(pos);
        final String selectedKey = selectedItem.getKey();
        mDatabaseRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(paybillActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                } else {
                    String message = task.getException().toString();
                    Toast.makeText(paybillActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}