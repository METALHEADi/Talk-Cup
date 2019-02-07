package com.example.mohamed.clonewhatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
private Button SendVerficationCode ,Verfiy;
private EditText PhoneNumber,VerficationCode;
private FirebaseAuth mAuth;
private PhoneAuthCredential mPhoneAuth;
private PhoneAuthProvider mPhoneProvide;
private PhoneAuthProvider.ForceResendingToken mResendToken;
private String mVerificationId,CurrentUserID;
private ProgressDialog progressDialog;
private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
private DatabaseReference UserRef;
private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        InitializeField();
        SendVerficationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhoneNumberSt=PhoneNumber.getText().toString();
                if (TextUtils.isEmpty(PhoneNumberSt)){
                    Toast.makeText(PhoneLoginActivity.this, "Please Enter Your Phone Number", Toast.LENGTH_LONG).show();
                }else
                {
                    progressDialog.setTitle("Phone Verification ");
                    progressDialog.setMessage("Please Wait ");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mPhoneProvide.verifyPhoneNumber(PhoneNumberSt, 60, TimeUnit.SECONDS, PhoneLoginActivity.this, mCallBack);


                }
            }
        });
        Verfiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerficationCode.setVisibility(View.INVISIBLE);
                PhoneNumber.setVisibility(View.INVISIBLE);
                String verficationCode=VerficationCode.getText().toString();
                 if(TextUtils.isEmpty(verficationCode)){
                Toast.makeText(PhoneLoginActivity.this, "Please write verfication Code ....", Toast.LENGTH_SHORT).show();
            }else {
                     progressDialog.setTitle(" Verification Code");
                     progressDialog.setMessage("Please Wait ");
                     progressDialog.setCanceledOnTouchOutside(false);
                     progressDialog.show();
                     PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verficationCode);
                     signInWithPhoneAuthCredential(credential);

                 }

            }
        });


    }

    private void InitializeField() {
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
    SendVerficationCode=findViewById(R.id.register_phone_btn);
    Verfiy=findViewById(R.id.verfication_phone_btn);
    PhoneNumber=findViewById(R.id.enter_phone_num);
    VerficationCode=findViewById(R.id.enter_verification_num);
    mAuth=FirebaseAuth.getInstance();
    mPhoneProvide=PhoneAuthProvider.getInstance();
    Verfiy.setVisibility(View.INVISIBLE);
    VerficationCode.setVisibility(View.INVISIBLE);
    progressDialog=new ProgressDialog(this);
    mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    @Override
    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
        progressDialog.dismiss();

        Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number , Please Enter Correct Phone Number with your Country code.", Toast.LENGTH_SHORT).show();
        SendVerficationCode.setVisibility(View.VISIBLE);
        PhoneNumber.setVisibility(View.VISIBLE);
        Verfiy.setVisibility(View.INVISIBLE);
        VerficationCode.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        mVerificationId = s;
        mResendToken = forceResendingToken;
        Toast.makeText(PhoneLoginActivity.this, "Code has been Sent", Toast.LENGTH_SHORT).show();
        SendVerficationCode.setVisibility(View.INVISIBLE);
        PhoneNumber.setVisibility(View.INVISIBLE);
        Verfiy.setVisibility(View.VISIBLE);
        VerficationCode.setVisibility(View.VISIBLE);
        progressDialog.dismiss();

    }

};}

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "You Are Logged in Successfuly ", Toast.LENGTH_SHORT).show();
                            String DeviceToken= FirebaseInstanceId.getInstance().getToken();
                            CurrentUserID=mAuth.getCurrentUser().getUid();

                            UserRef.child(CurrentUserID).child("DeviceToken").setValue(DeviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
//                                        Toast.makeText(getApplicationContext(),"Signing in is done Sucessfully !",Toast.LENGTH_LONG).show();

                                        SendUserToMainActivity();

                                    }
                                }
                            });

//                            SendUserToMainActivity();
                        } else {
                            progressDialog.dismiss();

                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent intent=new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
