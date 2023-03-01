package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class cartProductActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DatabaseReference reference;
    private ArrayList<product> list;
    private RecyclerView recyclerView;
    private SearchView searchView;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_product);

        mToolbar=(Toolbar)findViewById(R.id.cartProduct_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Cart Product");

        Initialization();



    }

    @Override
    protected void onStart() {
        super.onStart();

        if (reference!=null){
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){

                        list=new ArrayList<>();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            product p=ds.getValue(product.class);
                            p.setKey(ds.getKey().toString());
                            list.add(p);
                        }
                        cartAdapterClass adapterClass=new cartAdapterClass(list);
                        recyclerView.setAdapter(adapterClass);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(cartProductActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

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

        ArrayList<product>mylist=new ArrayList<>();

        for(product object : list){
            String x= object.getKey()+object.getName();
            if(x.toLowerCase().contains(str.toLowerCase())){
                mylist.add(object);
            }

        }
        cartAdapterClass adapterClass=new cartAdapterClass(mylist);
        recyclerView.setAdapter(adapterClass);

    }



    private void Initialization() {

        Intent i=this.getIntent();
        uid=i.getExtras().getString("uid");

        reference= FirebaseDatabase.getInstance().getReference().child("Cart").child(uid);
        recyclerView=findViewById(R.id.rvcartProduct);
        searchView=findViewById(R.id.cartProductsearchView);
    }
}