package com.example.mohamed.clonewhatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private TextView already_have_account;
    private EditText Email_ed_Register,Password_ed_register;
    private Button SignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;
    private DatabaseReference mDBref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitializeFields();
        already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SendUserToLoginActivity();
            }
        });
    SignUp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CreateNewAccount();
        }
    });
    }

    private void CreateNewAccount()
    {
        progressDialog.show();

        final String Email_reg_ST=Email_ed_Register.getText().toString();
        final String Password_reg_ST=Password_ed_register.getText().toString();

        if(TextUtils.isEmpty(Email_reg_ST)){

            Toast.makeText(this,"Please Enter Your Email Address",Toast.LENGTH_LONG).show();
                    }
        if (Password_reg_ST.length()<6){
        Toast.makeText(this,"Please Make your Password Longer than 6 Characters",Toast.LENGTH_LONG).show(); }
else {

        mAuth.createUserWithEmailAndPassword(Email_reg_ST,Password_reg_ST)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if (task.isSuccessful()){
                    String DeviceToken= FirebaseInstanceId.getInstance().getToken();

                    String ID_user=mAuth.getCurrentUser().getUid();
                    mDBref.child("Users").child(ID_user).setValue("");
                    mDBref.child("Users").child(ID_user).child("Email").setValue(Email_reg_ST);
                    mDBref.child("Users").child(ID_user).child("Password").setValue(Password_reg_ST);
                    mDBref.child("Users").child(ID_user).child("DeviceToken").setValue(DeviceToken);

                    SendUserToLoginActivity();
                    Toast.makeText(RegisterActivity.this,"Your Registering is Done ",Toast.LENGTH_LONG).show();


                }else {

                    Toast.makeText(RegisterActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                }

            }
        });


        }

    }

    private void SendUserToLoginActivity() {
        Intent Login_intent=new Intent(RegisterActivity.this,LoginActivity.class);
        Login_intent.putExtra("Email",Email_ed_Register.getText().toString());
        startActivity(Login_intent) ;
    }



    private void InitializeFields() {
        Email_ed_Register=findViewById(R.id.signup_email);
        Password_ed_register=findViewById(R.id.signup_password);
        SignUp=findViewById(R.id.sigup_button);
        already_have_account=findViewById(R.id.already_have_account);
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Wait Please ... ");
        progressDialog.setMessage("Creating your Account ");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        mDBref= FirebaseDatabase.getInstance().getReference();


    }

}
