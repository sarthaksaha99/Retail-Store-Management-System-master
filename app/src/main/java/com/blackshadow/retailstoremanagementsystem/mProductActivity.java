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
import android.view.Menu;
import android.view.MenuItem;
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

public class mProductActivity extends AppCompatActivity implements CartRecyclerAdapter.OnItemClickListener{
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private RecyclerView mRecyclerView;
    private CartRecyclerAdapter mAdapter;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<product> mProducts;
    private List<String> allprice;
    private SearchView searchView;

    String currentUserId;


    private void openDetailActivity(String[] data){
        Intent intent = new Intent(this, mproductDetailsActivity.class);
        intent.putExtra("NAME_KEY",data[0]);
        intent.putExtra("DESCRIPTION_KEY",data[1]);
        intent.putExtra("IMAGE_KEY",data[2]);
        intent.putExtra("Product_KEY",data[3]);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_product);

        mAuth=FirebaseAuth.getInstance();

        currentUser=mAuth.getCurrentUser();
        currentUserId=mAuth.getCurrentUser().getUid();


        mToolbar=(Toolbar)findViewById(R.id.mProduct_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Product");
        searchView=findViewById(R.id.mproductsearchView);

        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.mProductProgressBar);
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
        mAdapter = new CartRecyclerAdapter (mProductActivity.this, mProducts);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(mProductActivity.this);

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
                Toast.makeText(mProductActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        CartRecyclerAdapter adapterClass=new CartRecyclerAdapter(mProductActivity.this,mylist);
//        mProducts=mylist;
        mRecyclerView.setAdapter(adapterClass);
        adapterClass.setOnItemClickListener(mProductActivity.this);

    }

    public void onItemClick(int position) {
        product clickedProduct=mProducts.get(position);
        String[] productData={clickedProduct.getName(),clickedProduct.getPrice(),clickedProduct.getImageUrl(),clickedProduct.getKey()};
        openDetailActivity(productData);
    }

    @Override
    public void onShowItemClick(int position) {
        product clickedProduct=mProducts.get(position);
        String[] productData={clickedProduct.getName(),clickedProduct.getPrice(),clickedProduct.getImageUrl(),clickedProduct.getKey()};
        openDetailActivity(productData);
    }

    @Override
    public void onDeleteItemClick(int position) {
        product selectedItem = mProducts.get(position);
        final String selectedKey = selectedItem.getKey();

//        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
//        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                mDatabaseRef.child(selectedKey).removeValue();
//                Toast.makeText(mProductActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
//            }
//        });
        mDatabaseRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mProductActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                } else {
                    String message = task.getException().toString();
                    Toast.makeText(mProductActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onButtonClick(int pos) {
        product selectedItem = mProducts.get(pos);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(mProductActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_product,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.add_product_item){
            addProductActivity();
        }
        return true;
    }

    private void addProductActivity() {
        Intent AddProductIntent= new Intent(mProductActivity.this,newProductActivity.class);
//        AddProductIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(AddProductIntent);
//        finish();
    }
}