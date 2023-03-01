package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class productActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClickListener{
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,refAdminAccess;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<product> mProducts;
    private SearchView searchView;
    String currentUserId;

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
        setContentView(R.layout.activity_product);

        mAuth=FirebaseAuth.getInstance();

        currentUser=mAuth.getCurrentUser();
        currentUserId=mAuth.getCurrentUser().getUid();
        ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(currentUserId);


        mToolbar=(Toolbar)findViewById(R.id.product_activity_toolbar);
        searchView=findViewById(R.id.productsearchView);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Products");



        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.myDataLoaderProgressBar);
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
        mAdapter = new RecyclerAdapter (productActivity.this, mProducts);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(productActivity.this);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Product");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProducts.clear();

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    product upload = productSnapshot.getValue(product.class);
                    upload.setKey(productSnapshot.getKey());
                    mProducts.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(productActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        if (searchView!=null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
    }

    private void search(String str){

        ArrayList<product> mylist=new ArrayList<>();

        for(product object : mProducts){

            String x= object.getName();
            if(x.toLowerCase().contains(str.toLowerCase())){
                mylist.add(object);
            }

        }
        RecyclerAdapter adapterClass=new RecyclerAdapter(productActivity.this,mylist);
        mRecyclerView.setAdapter(adapterClass);
        adapterClass.setOnItemClickListener(productActivity.this);

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

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(productActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onButtonClick(int pos) {
        product clickedProduct=mProducts.get(pos);

        final HashMap<String, String> profileMap = new HashMap<>();
//        profileMap.put("uid", currentUserId);
        profileMap.put("name", clickedProduct.getName());
        profileMap.put("price", clickedProduct.getPrice());
        profileMap.put("imageUrl", clickedProduct.getImageUrl());
        profileMap.put("key", clickedProduct.getKey());
        String uploadId = ref.push().getKey();
        ref.child(uploadId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(productActivity.this,"Added to Cart",Toast.LENGTH_SHORT).show();
                }else{
                    String message= task.getException().toString();
                    Toast.makeText(productActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}