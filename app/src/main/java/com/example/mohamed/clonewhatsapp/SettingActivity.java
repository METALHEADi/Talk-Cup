package com.example.mohamed.clonewhatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button UpdateSettingButten;
    private EditText NameSetting,StatueSetting;
    private CircleImageView ProfilePic;
    private String CurrentUSerID;
    private FirebaseAuth mAuth_set;
    private DatabaseReference RootRef;
    private Uri filePath;
    FirebaseStorage storage;
    private StorageReference UserProfileImageRef;
    private StorageReference FilePathSR;
    private android.support.v7.widget.Toolbar toolbar;

    private final int PICK_IMAGE_REQUEST = 71;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        InitializeField();
        RetriveUserInfo();
//        GetImage();
        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            chooseImage();
            }
        });
        UpdateSettingButten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();


            }
        });
    }

    private void RetriveUserInfo() {
    RootRef.child("Users").child(CurrentUSerID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists() && dataSnapshot.hasChild("Name")&&dataSnapshot.hasChild("Image")){

                String RetriveNameInfo=dataSnapshot.child("Name").getValue().toString();
                String RetriveStatusInfo=dataSnapshot.child("Status").getValue().toString();
                String RetriveImageInfo=dataSnapshot.child("Image").getValue().toString();
                NameSetting.setText(RetriveNameInfo);
                StatueSetting.setText(RetriveStatusInfo);
                Picasso.get().load(RetriveImageInfo).into(ProfilePic);



            }else  if (dataSnapshot.exists() && dataSnapshot.hasChild("Name")){
                String RetriveNameInfo=dataSnapshot.child("Name").getValue().toString();
                String RetriveStatusInfo=dataSnapshot.child("Status").getValue().toString();
                NameSetting.setText(RetriveNameInfo);
                StatueSetting.setText(RetriveStatusInfo);

            }else {
                Toast.makeText(SettingActivity.this, "Please Set and Update your Info", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });


    }

    private void UpdateSetting() {
        String NameSetting_ST=NameSetting.getText().toString();
        String StatueSetting_ST=StatueSetting.getText().toString();
        if (TextUtils.isEmpty(NameSetting_ST)){
            Toast.makeText(SettingActivity.this, "Please write your Name", Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(StatueSetting_ST)){
            Toast.makeText(SettingActivity.this, "Please write your Statue", Toast.LENGTH_SHORT).show();
        }else {
            Map<String,Object> profileMap=new HashMap<>();
            profileMap.put("UID",CurrentUSerID);
            profileMap.put("Name",NameSetting_ST);
            profileMap.put("Status",StatueSetting_ST);
            RootRef.child("Users").child(CurrentUSerID).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SettingActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        SendUserToMainActivity();

                    }else {

                        Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Toast.makeText(SettingActivity.this, "Updated", Toast.LENGTH_SHORT).show();

        }
    }

    private void InitializeField() {
        toolbar=findViewById(R.id.setting_pagelayout);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Setting ");
    UpdateSettingButten=findViewById(R.id.update_setting);
    NameSetting=findViewById(R.id.user_name);
    StatueSetting=findViewById(R.id.user_Statue);
    ProfilePic=findViewById(R.id.profile_image);
    mAuth_set=FirebaseAuth.getInstance();
    CurrentUSerID=mAuth_set.getCurrentUser().getUid().toString();
    RootRef= FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//         forestRef = storageReference.child("images/"+CurrentUSerID+".jpeg");
        UserProfileImageRef=FirebaseStorage.getInstance().getReference().child("Profiles Images");
        FilePathSR=UserProfileImageRef.child(CurrentUSerID+".jpg");


    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Toast.makeText(this, "dddd", Toast.LENGTH_SHORT).show();

                if(resultCode==RESULT_OK){
                    Uri resultUri = result.getUri();
                    FilePathSR.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return FilePathSR.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downUri = task.getResult();
                                String PP=downUri.toString();
                                RootRef.child("Users").child(CurrentUSerID).child("Image").setValue(PP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SettingActivity.this, "Image Saved in DataBase", Toast.LENGTH_SHORT).show();
                                    }     else {
                                        Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }}});}

//                    FilePathSR.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                            if (task.isSuccessful()){
//                                final String PPdownloadUrl=task.getResult().getStorage().getDownloadUrl().toString();
//                                RootRef.child("Users").child(CurrentUSerID).child("Image").setValue(PPdownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        Toast.makeText(SettingActivity.this, "Image Saved in DataBase", Toast.LENGTH_SHORT).show();
//                                    }     else {
//                                        Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                    }
//                                });
//                                Toast.makeText(SettingActivity.this, "your Profile Picture uploaded Sucessfuly ", Toast.LENGTH_SHORT).show();
//
//                            }else {
//                                Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
//
//                            }
                        }
                    });

                }

            }



//            filePath = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                ProfilePic.setImageBitmap(bitmap);
//                uploadImage();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
        }

//    private void uploadImage() {
//
//        if(filePath != null)
//        {
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//
//            StorageReference ref = storageReference.child("images/"+CurrentUSerID);
//            ref.putFile(filePath)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(SettingActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(SettingActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                        }
//                    });
//        }
//    }

    private void SendUserToMainActivity() {
        Intent SettoMainActivityIntent =new Intent(SettingActivity.this,MainActivity.class);
        SettoMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SettoMainActivityIntent);
        finish();
    }
//private void GetImage(){
//    forestRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//        @Override
//        public void onSuccess(StorageMetadata storageMetadata) {
//
//            String ss=storageMetadata.getPath();
//            Toast.makeText(SettingActivity.this, "ss", Toast.LENGTH_SHORT).show();
////            ProfilePic.setImageURI(ss.);
//        }
//    }).addOnFailureListener(new OnFailureListener() {
//        @Override
//        public void onFailure(@NonNull Exception exception) {
//            // Uh-oh, an error occurred!
//        }
//    });
//
//
//}

}