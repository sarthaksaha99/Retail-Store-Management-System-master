package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class managerListActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DatabaseReference reference;
    ArrayList<Users> list;
    private RecyclerView recyclerView;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_list);

        Initialization();
        mToolbar=(Toolbar)findViewById(R.id.mml_activity_toolbar);
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
                            if(users.getUser_type().toString().equals("2") || users.getUser_type().toString().equals("3"))
                            list.add(users);
                        }
                        mAdapterClass adapterClass=new mAdapterClass(list);
                        recyclerView.setAdapter(adapterClass);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(managerListActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
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
        mAdapterClass adapterClass=new mAdapterClass(mylist);
        recyclerView.setAdapter(adapterClass);

    }

    private void Initialization() {
        reference= FirebaseDatabase.getInstance().getReference().child("User");
        recyclerView=findViewById(R.id.rvmml);
        searchView=findViewById(R.id.mmlsearchView);
    }
}