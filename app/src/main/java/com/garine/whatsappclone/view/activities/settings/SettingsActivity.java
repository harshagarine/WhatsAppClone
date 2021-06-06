package com.garine.whatsappclone.view.activities.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.garine.whatsappclone.R;
import com.garine.whatsappclone.databinding.ActivitySettingsBinding;
import com.garine.whatsappclone.view.activities.profile.ProfileActivity;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings);

        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser!=null){
            getInfo();
        }
        initClickAction();
    }

    private void initClickAction() {
        binding.lnProfile.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, ProfileActivity.class)));
    }

    private void getInfo(){
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            String userName = Objects.requireNonNull(documentSnapshot.get("userName")).toString();
            String imageProfile = documentSnapshot.getString("imageProfile");
            binding.tvUsername.setText(userName);
            Glide.with(SettingsActivity.this).load(imageProfile).into(binding.imageProfile);

        }).addOnFailureListener(e -> Log.d("Get Data", "onFailure: "+e.getMessage()));
    }
}
