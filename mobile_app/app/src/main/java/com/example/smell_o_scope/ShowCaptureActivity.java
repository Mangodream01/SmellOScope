package com.example.smell_o_scope;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ShowCaptureActivity extends AppCompatActivity {

    Bitmap bitmap;

    String Uid;

    public static Upload upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);

        try {
            bitmap = BitmapFactory.decodeStream(getApplication().openFileInput("imageToSend"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
            return;
        }

        ImageView mImage = findViewById(R.id.imageCaptured);

        mImage.setImageBitmap(bitmap);

        Uid = FirebaseAuth.getInstance().getUid();
        Button saveImage = findViewById(R.id.save_picture);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDatabase();
            }
        });

    }

    private void saveToDatabase() {

        final DatabaseReference userImageDb = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("capturedImages");
        final String key = userImageDb.push().getKey();

        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("captures").child(key);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20,baos);
        byte[] dataToUpload = baos.toByteArray();
        UploadTask uploadTask = filePath.putBytes(dataToUpload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map newImage = new HashMap();
                        newImage.put("profileImageUrl", uri.toString());
                        userImageDb.updateChildren(newImage);
                        Toast.makeText(ShowCaptureActivity.this, "Image saved to database", Toast.LENGTH_LONG).show();

                        upload.setmImageUrl(uri.toString());
                        finish();
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        finish();
                        return;
                    }
                });
            }
        });

//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
//                Uri imageUrl = urlTask.getResult();
//
//                Long currentTimeStamp = System.currentTimeMillis();
//                Long endTimeStamp = currentTimeStamp + (24*60*60*1000);
//
//                Map<String, Object> mapToUpload = new HashMap<>();
//                mapToUpload.put("imageUrl", imageUrl.toString());
//                mapToUpload.put("timestampBegin", currentTimeStamp.toString());
//                mapToUpload.put("timestampEnd", endTimeStamp.toString());
//
//                userImageDb.child(key).setValue(mapToUpload);
//                finish();
//                return;
//
//
//
//
//            }
//        });


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
                return;
            }
        });

    }


}
