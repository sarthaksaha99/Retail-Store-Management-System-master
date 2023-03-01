package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class customerTrakingActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DatabaseReference reference;
    ArrayList<Users> list;
    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_traking);



        Initialization();
        mToolbar=(Toolbar)findViewById(R.id.customer_List_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Customer List");

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
                            Users users=ds.getValue(Users.class);
                            if(users.getUser_type().toString().equals("0") || users.getUser_type().toString().equals("4"))
                                list.add(users);
                        }
                        AdapterClass adapterClass=new AdapterClass(list);
                        recyclerView.setAdapter(adapterClass);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(customerTrakingActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
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

        ArrayList<Users>mylist=new ArrayList<>();

        for(Users object : list){

            String x= object.getPhone()+object.getName();
            if(x.toLowerCase().contains(str.toLowerCase())){
                mylist.add(object);
            }

        }
        AdapterClass adapterClass=new AdapterClass(mylist);
        recyclerView.setAdapter(adapterClass);

    }

    private void Initialization() {
        reference= FirebaseDatabase.getInstance().getReference().child("User");
        recyclerView=findViewById(R.id.rvCustomer);
        searchView=findViewById(R.id.customersearchView);
    }
}