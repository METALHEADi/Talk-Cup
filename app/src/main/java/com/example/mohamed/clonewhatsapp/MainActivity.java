package com.example.mohamed.clonewhatsapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
  private android.support.v7.widget.Toolbar toolbar;
private ViewPager myViewPager;
private TabLayout myTabLayout;
private TabAccessAdopter tabAccessAdopter;
private FirebaseUser firebaseUser;
public ProgressDialog progressDialog;
private RelativeLayout relativeLayout;
private FirebaseAuth mAuth;
private DatabaseReference RootRef;
private String CurrentUserID;
Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitalizeFields();


    }

    private void InitalizeFields() {
        toolbar=findViewById(R.id.mainpagelayout);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("T A L K - C U P");
        myViewPager =findViewById(R.id.main_tab_pager);
        tabAccessAdopter=new TabAccessAdopter(getSupportFragmentManager());
        myViewPager.setAdapter(tabAccessAdopter);
        myTabLayout=findViewById(R.id.main_tab);
        myTabLayout.setupWithViewPager(myViewPager);
        relativeLayout=findViewById(R.id.Maaaaaaa);
        mAuth= FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        RootRef= FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_find_friends_option :{
                SendUserToFindFriend();
                break;}
            case R.id.main_Setting_option :{
                SendUserToSettingActivity();
                break;}
            case R.id.main_Sign_Out_option :{
                mAuth.signOut();
                Toast.makeText(this,"You Signed Out",Toast.LENGTH_LONG).show();
                SendUserToLogin();
                break;}
            case R.id.main_create_group_option :{
                RequestCreateNewGroup();
                break;}

        }
        return true;
    }

    private void SendUserToFindFriend() {
    Intent intent=new Intent(MainActivity.this,FindFriendActivity.class);
    startActivity(intent);

    }

    private void RequestCreateNewGroup() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        final EditText groupNameField =new EditText(this);
        groupNameField.setHint("e.g Hello Community");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create Group", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           String groupNameField_ST=groupNameField.getText().toString();
           if (TextUtils.isEmpty(groupNameField_ST)){

               Toast.makeText(MainActivity.this, "Please Enter Your  Group name ", Toast.LENGTH_SHORT).show();
           }else
           {
                CreateNewGroup(groupNameField_ST);

           }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           dialog.cancel();
            }
        });
        builder.show();


    }

    private void CreateNewGroup(final String groupName) {
    RootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
                Toast.makeText(MainActivity.this, groupName+" is created successfully", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    });

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait  , Loading ");
        progressDialog.setTitle("Connecting Server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        if(firebaseUser==null){
            SendUserToLogin();

        }else {
            CurrentUserID=mAuth.getCurrentUser().getUid();

            VerifyUserExsitance();
            updateStatus("online");
        }
        progressDialog.cancel();
        Snackbar.make(relativeLayout,"You are Logged in now  ",Snackbar.LENGTH_LONG).show();

    }

    private void VerifyUserExsitance() {
         String currentUserID=mAuth.getCurrentUser().getUid();
RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.child("Name").exists()){
            Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();

        }else{

            Intent setting_intent=new Intent(MainActivity.this,SettingActivity.class);
            setting_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(setting_intent) ;
            Toast.makeText(MainActivity.this, "Please Compelte Your Info First", Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});

    }

    private void SendUserToLogin() {
        Intent loginIntent =new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(loginIntent);

        progressDialog.cancel();
        finish();
    }
    private void SendUserToSettingActivity() {
        Intent setting_intent=new Intent(MainActivity.this,SettingActivity.class);
        startActivity(setting_intent) ;
    }
    private void updateStatus(String state){

        String saveCurrentTime,saveCurrentDate;
        Calendar calendar =Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("dd,MMM,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime =new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        HashMap<String,Object> OnlineState=new HashMap();
        OnlineState.put("time",saveCurrentTime);
        OnlineState.put("date",saveCurrentDate);
        OnlineState.put("state",state);
        RootRef.child("Users").child(CurrentUserID).child("UserState").updateChildren(OnlineState);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseUser==null){


        }else {
            updateStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(firebaseUser==null){


        }else {
            updateStatus("offline");
        }
    }
}
