package com.blackshadow.retailstoremanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class mproductDetailsActivity extends AppCompatActivity {
    private EditText nameDetail,descriptionDetail;
    private ImageView productDetailImageView;
    private Button updatepro;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference ref,reference;
    String key,imageURL;

    private void initializeWidgets(){
        nameDetail= (EditText)findViewById(R.id.et_product_name);
        descriptionDetail= (EditText)findViewById(R.id.et_pr_price);
        productDetailImageView=findViewById(R.id.mproductDetailImageView);
        updatepro=(Button)findViewById(R.id.update_product);
    }
    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date=new Date();
        String today= dateFormat.format(date);
        return today;
    }
    private String getRandomCategory(){
        String[] categories={"ZEN","BUDHIST","YOGA"};
        Random random=new Random();
        int index=random.nextInt(categories.length-1);
        return categories[index];
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mproduct_details);

        mAuth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("Product");
        currentUser=mAuth.getCurrentUser();

        mToolbar=(Toolbar)findViewById(R.id.mproduct_details__activity_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Products Details");

        initializeWidgets();

        //RECEIVE DATA FROM ITEMSACTIVITY VIA INTENT
        Intent i=this.getIntent();
        String name=i.getExtras().getString("NAME_KEY");
        String description=i.getExtras().getString("DESCRIPTION_KEY");
        imageURL=i.getExtras().getString("IMAGE_KEY");
        key=i.getExtras().getString("Product_KEY");

        //SET RECEIVED DATA TO TEXTVIEWS AND IMAGEVIEWS
        nameDetail.setText(name);
        descriptionDetail.setText(description);
        Picasso.get()
                .load(imageURL)
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(productDetailImageView);

        updatepro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_info_function();
            }
        });
    }

    private void edit_info_function() {
        final String updated_name= nameDetail.getText().toString();
        final String updated_price= descriptionDetail.getText().toString();


        if (TextUtils.isEmpty(updated_name)|| TextUtils.isEmpty(updated_price)){
            Toast.makeText(mproductDetailsActivity.this,"Set name and phone number correctly",Toast.LENGTH_SHORT).show();
        }
        else{
            final HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("name", updated_name);
            profileMap.put("price", updated_price);
            profileMap.put("imageUrl", imageURL);
//                profileMap.put("access","Not Checked");

            reference.child(key).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        nameDetail.setText(updated_name);
                        descriptionDetail.setText(updated_price);
                        Toast.makeText(mproductDetailsActivity.this, "Product Updated Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(mproductDetailsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}