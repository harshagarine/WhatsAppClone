package com.garine.whatsappclone.view.activities.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.garine.whatsappclone.BuildConfig;
import com.garine.whatsappclone.R;
import com.garine.whatsappclone.common.Common;
import com.garine.whatsappclone.databinding.ActivityProfileBinding;
import com.garine.whatsappclone.view.activities.display.ViewImageActivity;
import com.garine.whatsappclone.view.activities.startup.SplashScreenActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private BottomSheetDialog bottomSheetDialog, bsDialogEditName;
    private ProgressDialog progressDialog;

    private final int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        if (firebaseUser!=null){
            getInfo();
        }

        initActionClick();

    }

    private void initActionClick() {
        binding.fabCamera.setOnClickListener(v -> showBottomSheetPickPhoto());

        binding.lnEditName.setOnClickListener(v -> showBottomSheetEditName());

        binding.imageProfile.setOnClickListener(v -> {
            binding.imageProfile.invalidate();
            Drawable dr = binding.imageProfile.getDrawable();
            Common.IMAGE_BITMAP = ((GlideBitmapDrawable)dr.getCurrent()).getBitmap();
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this, binding.imageProfile, "image");
            Intent intent = new Intent(ProfileActivity.this, ViewImageActivity.class);
            startActivity(intent, activityOptionsCompat.toBundle());


        });

        binding.btnLogOut.setOnClickListener(v -> showDialogSignOut());

    }

    private void showBottomSheetPickPhoto() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener((View.OnClickListener) view1 -> {
            openGallery();
            bottomSheetDialog.dismiss();
        });
        ((View) view.findViewById(R.id.ln_camera)).setOnClickListener((View.OnClickListener) view12 -> {

            //ToDo Open Camera
            checkCameraPermission();


            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(dialog -> bottomSheetDialog=null);

        bottomSheetDialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    221);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    222);
        }
        else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {
            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,  imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showBottomSheetEditName(){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);

        ((View) view.findViewById(R.id.btn_cancel)).setOnClickListener((View.OnClickListener) view1 -> bsDialogEditName.dismiss());

        final EditText edUserName = view.findViewById(R.id.ed_username);

        ((View) view.findViewById(R.id.btn_save)).setOnClickListener((View.OnClickListener) view12 -> {
            if (TextUtils.isEmpty(edUserName.getText().toString())){
                Toast.makeText(getApplicationContext(),"Name can'e be empty",Toast.LENGTH_SHORT).show();
            } else {
                updateName(edUserName.getText().toString());
                bsDialogEditName.dismiss();
            }
        });

        bsDialogEditName = new BottomSheetDialog(this);
        bsDialogEditName.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bsDialogEditName.setOnDismissListener(dialog -> bsDialogEditName = null);

        bsDialogEditName.show();
    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            String userName = documentSnapshot.getString("userName");
            String userPhone = documentSnapshot.getString("userPhone");
            String imageProfile = documentSnapshot.getString("imageProfile");

            binding.tvUsername.setText(userName);
            binding.tvPhone.setText(userPhone);
            Glide.with(ProfileActivity.this).load(imageProfile).into(binding.imageProfile);


        }).addOnFailureListener(e -> {

        });
    }

    private void openGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            imageUri = data.getData();

            uploadToFirebase();
        }

        if (requestCode == 440
                && resultCode == RESULT_OK){
            uploadToFirebase();
        }

    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadToFirebase() {
        if (imageUri!=null){
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImagesProfile/" + System.currentTimeMillis()+"."+getFileExtention(imageUri));
            riversRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();

                final String sdownload_url = String.valueOf(downloadUrl);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageProfile", sdownload_url);

                progressDialog.dismiss();
                firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(),"upload successfully",Toast.LENGTH_SHORT).show();

                    getInfo();
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(),"upload Failed",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        }
    }

    private void updateName(String newName){
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName",newName).addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplicationContext(),"Update Successful",Toast.LENGTH_SHORT).show();
            getInfo();
        });
    }

    private void showDialogSignOut(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("Do you want to sign out?");
        builder.setPositiveButton("Sign out", (dialog, which) -> {
            dialog.cancel();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, SplashScreenActivity.class));
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
