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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
//    private FirebaseUser firebaseUser;
    private TextView need_new_account,Forget_Password;
    private Button SignIn ,SignInPhone;
    private EditText Email_ed,Password_ed;
    private RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent i1=getIntent();
        InitializeFields();
        if (getIntent().getExtras()!=null){
            String IntentDataEmail=i1.getExtras().getString("Email");
            Email_ed.setText(IntentDataEmail);
            }

        Snackbar.make(relativeLayout,"Sign In now ",Snackbar.LENGTH_LONG).show();

        need_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();     }
        });
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUserEmail_PW();

            }
        });
        SignInPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void CheckUserEmail_PW() {
        final String Email_Signin_ST=Email_ed.getText().toString();
        String Password_Signin_ST=Password_ed.getText().toString();
        progressDialog.show();
        if(TextUtils.isEmpty(Email_Signin_ST)){
        progressDialog.dismiss();
            Toast.makeText(this,"Please Enter Your Email Address",Toast.LENGTH_LONG).show();

        }
        if (TextUtils.isEmpty(Password_Signin_ST))
        {
            progressDialog.dismiss();
            Toast.makeText(this,"Please Enter Your Password",Toast.LENGTH_LONG).show();
        }
        else
            {
            mAuth.signInWithEmailAndPassword(Email_Signin_ST,Password_Signin_ST).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        String CurrentUserID=mAuth.getCurrentUser().getUid();
                        String DeviceToken= FirebaseInstanceId.getInstance().getToken();
                        UserRef.child(CurrentUserID).child("DeviceToken").setValue(DeviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               progressDialog.dismiss();
                               Toast.makeText(LoginActivity.this,"Signing in is done Sucessfully !",Toast.LENGTH_LONG).show();

                               SendUserToMainActivity();

                           }
                            }
                        });

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                         }
                }
            });
        }
    }

    private void InitializeFields() {
        relativeLayout=findViewById(R.id.loginActiv);
        need_new_account=findViewById(R.id.need_new_account_tv);
        Forget_Password=findViewById(R.id.forget_password_tv);
        SignIn=findViewById(R.id.sigin_button);
        SignInPhone=findViewById(R.id.phone_btn);
        Email_ed=findViewById(R.id.login_email);
        Password_ed=findViewById(R.id.login_password);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Wait Please ... ");
        progressDialog.setMessage("Signing in ... ");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth=FirebaseAuth.getInstance();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
//        firebaseUser=mAuth.getCurrentUser();

    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(firebaseUser!=null){
//            SendUserToMainActivity();
//        }


//    }
    private void SendUserToMainActivity() {
        Intent MainActivityIntent =new Intent(LoginActivity.this,MainActivity.class);
        MainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainActivityIntent);
        finish();
    }
    private void SendUserToRegisterActivity() {
        Intent RegisterIntent =new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(RegisterIntent);
    }
}
