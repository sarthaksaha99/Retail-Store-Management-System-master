package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class historyActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,refPaid;
    String currentUserId;

    private ListView history_view;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mAuth= FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        currentUserId=mAuth.getCurrentUser().getUid();
        ref= FirebaseDatabase.getInstance().getReference().child("Cart").child(currentUserId);
        refPaid= FirebaseDatabase.getInstance().getReference().child("Paid").child(currentUserId);


        mToolbar=(Toolbar)findViewById(R.id.history_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("History");

        initializations();
        retriveDateView();
    }

    private void retriveDateView() {
        refPaid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set=new HashSet<>();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    product upload = productSnapshot.getValue(product.class);
                    upload.setKey(productSnapshot.getKey());
                    set.add("Bought a "+upload.getName().toString()+" price "+upload.getPrice()+" at "+productSnapshot.child("pay_date").getValue());
                }

                list_of_groups.addAll(set);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializations() {
        history_view=(ListView)findViewById(R.id.list_view_history);

        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_groups);
        history_view.setAdapter(arrayAdapter);
    }
}