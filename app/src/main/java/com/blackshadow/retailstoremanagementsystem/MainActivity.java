package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,refAdminAccess;
    private LinearLayout profile, paybill, history, product,user,am,mprofile,addproduct,cushis,mproduct,manager,adminaccess,msg;

    public String UserStatus,UserName,category,currentUserId,currentUserGmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Initialization();

        mAuth=FirebaseAuth.getInstance();
        ref= FirebaseDatabase.getInstance().getReference();
        currentUser=mAuth.getCurrentUser();

        mToolbar=(Toolbar)findViewById(R.id.main_activity_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");




    }
    private void Initialization() {
        profile= (LinearLayout)findViewById(R.id.btnViewProfile);
        paybill= (LinearLayout)findViewById(R.id.btnPayBills);
        history= (LinearLayout)findViewById(R.id.btnHistory);
        product= (LinearLayout)findViewById(R.id.btnViewProduct);
        mprofile= (LinearLayout)findViewById(R.id.btnViewProfilem);
        addproduct= (LinearLayout)findViewById(R.id.btnAddProduct);
        cushis= (LinearLayout)findViewById(R.id.btnHistorym);
        mproduct= (LinearLayout)findViewById(R.id.btnViewProductm);
        user= (LinearLayout)findViewById(R.id.user_layout);
        am= (LinearLayout)findViewById(R.id.am_layout);
        manager=(LinearLayout)findViewById(R.id.btnManager);
        adminaccess=(LinearLayout)findViewById(R.id.adminll);
        msg=(LinearLayout)findViewById(R.id.llmsg);

        user.setVisibility(View.INVISIBLE);
        am.setVisibility(View.INVISIBLE);
        adminaccess.setVisibility(View.INVISIBLE);
        msg.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser!=null && currentUser.isEmailVerified()){
            VerifyUserExistance();
        }else {
            LoginActivity();
        }
    }

    private void  VerifyUserExistance() {
        currentUserId=mAuth.getCurrentUser().getUid();
        currentUserGmail=mAuth.getCurrentUser().getEmail();

        ref.child("User").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists()){
                    String ut=dataSnapshot.child("user_type").getValue().toString();
                    if(ut.equals("0")){
                        user.setVisibility(View.VISIBLE);
                        am.setVisibility(View.INVISIBLE);
                        msg.setVisibility(View.INVISIBLE);

                        profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ProfileActivity();
                            }
                        });
                        paybill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PaybillActivity();
                            }
                        });
                        history.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HistoryActivity();
                            }
                        });
                        product.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ProductActivity();
                            }
                        });
                    }
                    else if(ut.equals("1") || ut.equals("2"))
                    {
                        if (ut.equals("1"))
                        {
                            adminaccess.setVisibility(View.VISIBLE);
                            am.setVisibility(View.VISIBLE);
                            user.setVisibility(View.INVISIBLE);
                            msg.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            adminaccess.setVisibility(View.INVISIBLE);
                            am.setVisibility(View.VISIBLE);
                            user.setVisibility(View.INVISIBLE);
                            msg.setVisibility(View.INVISIBLE);
                        }


                        manager.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                manageActivity();
                            }
                        });
                        mproduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mProductActivity();
                            }
                        });
                        mprofile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mProfileActivity();
                            }
                        });
                        cushis.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mHistoryActivity();
                            }
                        });
                        addproduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AddProductActivity();
                            }
                        });
                    }
                    else
                    {
                        msg.setVisibility(View.VISIBLE);
                        user.setVisibility(View.INVISIBLE);
                        am.setVisibility(View.INVISIBLE);
                        adminaccess.setVisibility(View.INVISIBLE);
                    }
                }else{
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_items,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);


        if(item.getItemId() == R.id.main_logout_option){
            mAuth.signOut();
            LoginActivity();
        }

        return true;
    }

    private void LoginActivity() {
        Intent loginIntent= new Intent(MainActivity.this,loginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void ProfileActivity() {
        Intent profileIntent= new Intent(MainActivity.this,profileActivity.class);
//        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
//        finish();
    }
    private void ProductActivity() {
        Intent productIntent= new Intent(MainActivity.this,productActivity.class);
//        productIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(productIntent);
//        finish();
    }
    private void HistoryActivity() {
        Intent historyIntent= new Intent(MainActivity.this,paidProductActivity.class);
//        historyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        historyIntent.putExtra("uid",currentUserId);
        startActivity(historyIntent);
//        finish();
    }
    private void PaybillActivity() {
        Intent paybillIntent= new Intent(MainActivity.this,paybillActivity.class);
//        paybillIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(paybillIntent);
//        finish();
    }



    private void mProfileActivity() {
        Intent mprofileIntent= new Intent(MainActivity.this,profileActivity.class);
//        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mprofileIntent);
//        finish();
    }
    private void mProductActivity() {
        Intent mproductIntent= new Intent(MainActivity.this,mProductActivity.class);
//        productIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mproductIntent);
//        finish();
    }
    private void mHistoryActivity() {
        Intent mhistoryIntent= new Intent(MainActivity.this,customerTrakingActivity.class);
//        historyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(mhistoryIntent);
//        finish();
    }
    private void AddProductActivity() {
        Intent AddProductIntent= new Intent(MainActivity.this,newProductActivity.class);
//        AddProductIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(AddProductIntent);
//        finish();
    }

    private void manageActivity() {
        Intent manageIntent= new Intent(MainActivity.this,managerListActivity.class);
//        manageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(manageIntent);
//        finish();
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent= new Intent(MainActivity.this,settingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }
}