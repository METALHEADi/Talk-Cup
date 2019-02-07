package com.example.mohamed.clonewhatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class GroupChatActivity extends AppCompatActivity {
private String ChatGroupName,CurrentUserID,CurrentUserName,CurrentDate,CurrentTime;
private DatabaseReference GroupRef;
private FirebaseAuth mAuth;
private FirebaseUser firebaseUser;
private Toolbar toolbar;
private EditText UserMessageInput;
private ScrollView scrollView;
private ImageButton SendButton;
private TextView displayMessage;
private DatabaseReference RootRef,GroupMessageKEyRef;
private RecyclerView mRecyclerView;
private LinearLayoutManager manager;
private GroupChatAdapter adapter;
private final List<GroupChatMessage> list=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        ChatGroupName=getIntent().getExtras().get("NameOfGroupChatRoom").toString();
        InitializeFields();
//        scrollView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        },1000);

        GetUserInfo();
//        GroupRef.child(ChatGroupName).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.exists()) {
//                    DisplayMEssage(dataSnapshot);
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//               if(dataSnapshot.exists()) {
//                   DisplayMEssage(dataSnapshot);
//               }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        SaveMessageToDatabase();


                UserMessageInput.setText("");


            }
        });
gettingData();
    }

    private void gettingData() {
    GroupRef.child(ChatGroupName).addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            GroupChatMessage message=dataSnapshot.getValue(GroupChatMessage.class);
            list.add(message);
            adapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    private void SaveMessageToDatabase() {
        String UserMessageInput_ST=UserMessageInput.getText().toString();
        String MessageKey=GroupRef.push().getKey();
        if(TextUtils.isEmpty(UserMessageInput_ST)){
            Toast.makeText(this, "Please write your message", Toast.LENGTH_SHORT).show();
        }else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat CDateFormat = new SimpleDateFormat("dd - MMM - YYYY");
            CurrentDate = CDateFormat.format(calForDate.getTime());
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat CTimeFormat = new SimpleDateFormat("hh:mm:ss a");
            CurrentTime = CTimeFormat.format(calForTime.getTime());

            GroupMessageKEyRef=GroupRef.child(ChatGroupName).child(MessageKey);
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("Name", CurrentUserName);
            profileMap.put("from", CurrentUserID);
            profileMap.put("Date", CurrentDate);
            profileMap.put("Time", CurrentTime);
            profileMap.put("Message", UserMessageInput_ST);



            GroupMessageKEyRef.updateChildren(profileMap);

        }

    }

    private void InitializeFields() {
        toolbar=findViewById(R.id.main_group_chat_pagelayout);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(ChatGroupName +"'s Room");
        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups");
        UserMessageInput=findViewById(R.id.sendMesssage_edit_text);
//        scrollView=findViewById(R.id.myScrollView);
        SendButton=findViewById(R.id.Send_button_image);
//        displayMessage=findViewById(R.id.group_chat_text_display);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        CurrentUserID=firebaseUser.getUid();
        RootRef=FirebaseDatabase.getInstance().getReference().child("Users");

        adapter=new GroupChatAdapter(list,ChatGroupName);
        mRecyclerView=findViewById(R.id.group5ra3ladm8i);
        manager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

    }

    private void GetUserInfo() {
        RootRef.child(CurrentUserID).child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CurrentUserName=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        GroupRef.child(ChatGroupName).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.exists()){
//                DisplayMEssage(dataSnapshot);
//                }else{
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.exists()){
//                    DisplayMEssage(dataSnapshot);
//                }else{
//
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    private void DisplayMEssage(DataSnapshot dataSnapshot) {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){

            String chatDate= (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMsg= (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName= (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime= (String) ((DataSnapshot)iterator.next()).getValue();
            String chatFrom= (String) ((DataSnapshot)iterator.next()).getValue();

            displayMessage.append(chatName+ ":\n" +chatMsg +"\n"+"     "+ chatTime + "    "+chatDate+ "\n \n");
                scrollView.fullScroll(scrollView.FOCUS_DOWN);

        }
    }
}
