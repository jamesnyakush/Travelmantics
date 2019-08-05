package com.example.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class Insert extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private static final int PICTURE_RESULT = 42;
    private DatabaseReference databaseReference;
    private EditText mTitle, mDesc, mPrice;
    TravelDeal deal;
    Button mUpload;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_activity);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chev);

        FirebaseUtil.openFbReference("traveldeals", this);
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;



        mTitle = findViewById(R.id.title);
        mDesc = findViewById(R.id.desc);
        mPrice = findViewById(R.id.price);
        imageView = findViewById(R.id.imageDeal);
        mUpload = findViewById(R.id.uploadDeal);
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "add images"), PICTURE_RESULT);
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDeal();
            }
        });

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        showImage(deal.getImageUrl());
        mTitle.setText(deal.getTitle());
        mDesc.setText(deal.getDescription());
        mPrice.setText(deal.getPrice());
    }

    private void insertDeal() {
        deal.setTitle(mTitle.getText().toString().trim());
        deal.setDescription(mDesc.getText().toString().trim());
        deal.setPrice(mPrice.getText().toString().trim());

        if (deal.getId() == null) {
            databaseReference.push().setValue(deal);        Intent intent = getIntent();

            Toast.makeText(this, "Saved Succesfully", Toast.LENGTH_SHORT).show();
            home();
        } else {
            databaseReference.child(deal.getId()).setValue(deal);
            Toast.makeText(this, "Edited Succesfully", Toast.LENGTH_SHORT).show();
            home();
        }

    }

    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Does Not Exist ", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(deal.getId()).removeValue();
        Toast.makeText(this, "Removed Successfully", Toast.LENGTH_SHORT).show();
        home();
    }

    private void home() {
        startActivity(new Intent(this, TravelDeals.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    deal.setImageUrl(url);
                    showImage(url);
                }
            });

        }
    }

    private void showImage(String url){
        if (url != null && url.isEmpty() == false){
            int width= Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }

}
