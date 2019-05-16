package com.example.smell_o_scope;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class GalleryActivity extends AppCompatActivity {
    // https://androidjson.com/retrieve-stored-images-firebase-storage/

//    private RecyclerView mRecylcerView;
//    private ImageAdapter mAdapter;
//
//    private DatabaseReference mDatabaseRef;
//    private StorageReference mStorageRef;
//    private List<Upload> mUploads;

    String Uid;


    private ImageView mImage;

    Button analytics;

    ArrayList<String> imageUrlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//        mRecylcerView = findViewById(R.id.galleryview);
//        mRecylcerView.setHasFixedSize(true);
//        mRecylcerView.setLayoutManager(new LinearLayoutManager(this));
//
//        mUploads = new ArrayList<>();

        Uid = FirebaseAuth.getInstance().getUid();

//        //mDatabaseRef = FirebaseDatabase.getInstance().getReference("capturedImages");
//        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("capturedImages");
//
//        mDatabaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String imageUrl = "";
//                for(DataSnapshot chatSnapshot : dataSnapshot.getChildren()){
//                    if(chatSnapshot.child("imageUrl").getValue() != null){
//                        imageUrl = chatSnapshot.child("imageUrl").getValue().toString(); }
//                        imageUrlList.add(imageUrl);
//                                               }
//                                           }
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
////                    Upload upload = postSnapshot.getValue(Upload.class);
////                    //upload.setmImageUrl(ShowCaptureActivity.upload.getImageUrl());
////                    mUploads.add(upload);
//////                    Upload upload = ShowCaptureActivity.upload;
//////                    mUploads.add(upload);
////                }
////
////                mAdapter = new ImageAdapter(GalleryActivity.this, mUploads);
////
////                mRecylcerView.setAdapter(mAdapter);
////            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(GalleryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        ImageView imview = (ImageView) findViewById(R.id.dish1);
        imview.setImageResource(0);
        Drawable draw = getResources().getDrawable(R.mipmap.food_image);
        imview.setImageDrawable(draw);


        analytics = findViewById(R.id.analytics1);
        analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnalytics();
            }
        });


    }

    private int imageIterator = 0;
    private void initializeDisplay() {
        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });
        final Handler handler = new Handler();
        final int delay = 5000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeImage();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void changeImage() {
        if(imageIterator == imageUrlList.size() - 1){
            finish();
            return;
        }
        imageIterator++;
        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);
    }

    private void showAnalytics() {
        Intent intent = new Intent(this, AnalyticsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;
    }
}
