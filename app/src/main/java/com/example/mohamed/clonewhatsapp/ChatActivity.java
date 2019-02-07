package com.example.mohamed.clonewhatsapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
private Toolbar toolbar;
private RecyclerView mRecyclerView;
private TextView UserNamet,LastSeen;
private CircleImageView ProfileImage;
private EditText SetMessage;
private ImageButton SendMessageButton;
private FirebaseAuth mAuth;
private DatabaseReference UserRef,PMessage,RootRef,UserState;
private String CurrentUserID,UserID,state,time,date;
private final List<Message> list=new ArrayList<>();
private LinearLayoutManager manager;
private MessageAdaptor adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    InitializeField();

    SendMessageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SendDataToDataBase();

        }
    });
    RetrieveData();
    }

    private void RetrieveData() {
        RootRef.child("Messages").child(CurrentUserID).child(UserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message=dataSnapshot.getValue(Message.class);
                list.add(message);
                adaptor.notifyDataSetChanged();
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

    private void SendDataToDataBase() {

        String message=SetMessage.getText().toString();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please Enter Your Message First", Toast.LENGTH_SHORT).show();
        }else{

        String messageSendRef="Messages/"+CurrentUserID+"/"+UserID;
        String messageReciveRef="Messages/"+UserID+"/"+CurrentUserID;
        DatabaseReference userMessageKeyRef=RootRef.child("Messages").child(CurrentUserID).child(UserID).push();
        String UserMessagePush=userMessageKeyRef.getKey();
            Map map=new HashMap<>();
            map.put("message",message);
            map.put("type","text");
            map.put("from",CurrentUserID);
            Map MessageBodyDetails=new HashMap();
            MessageBodyDetails.put(messageSendRef+"/"+UserMessagePush,map);
            MessageBodyDetails.put(messageReciveRef+"/"+UserMessagePush,map);
            RootRef.updateChildren(MessageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
               if (task.isSuccessful()){


               }   else{

                   Toast.makeText(ChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
               }
               SetMessage.setText("");
                }
            });

        }
    }

    private void InitializeField() {
        UserID=getIntent().getExtras().get("UserID").toString();
        String UserName=getIntent().getExtras().get("UserName").toString();
        String UserImage=getIntent().getExtras().get("UserImage").toString();
        Toast.makeText(this, UserImage, Toast.LENGTH_LONG).show();
        toolbar=findViewById(R.id.Chatt_pagelayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.custom_tool_bar,null);
        getSupportActionBar().setCustomView(view);
        UserNamet=findViewById(R.id.username_custom_barh);
        LastSeen=findViewById(R.id.last_seen);
        ProfileImage=findViewById(R.id.profile_s);

        UserState=FirebaseDatabase.getInstance().getReference().child("Users").child(UserID).child("UserState");
        UserState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(ChatActivity.this, UserID, Toast.LENGTH_LONG).show();

                if (dataSnapshot.hasChild("time")&&dataSnapshot.hasChild("state")&&dataSnapshot.hasChild("date"))    {
                time=dataSnapshot.child("time").getValue().toString();
                date=dataSnapshot.child("date").getValue().toString();
                state=dataSnapshot.child("state").getValue().toString();
                    Toast.makeText(ChatActivity.this, time+date+state, Toast.LENGTH_LONG).show();
        if (state.equals("online")){
            LastSeen.setText("online now");
        }else if (state.equals("offline")){
            LastSeen.setText("Last Seen:"+time+" "+date);
        }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Picasso.get().load(UserImage).placeholder(R.drawable.profile_image).into(ProfileImage);
        UserNamet.setText(UserName);

        SetMessage=findViewById(R.id.sendMesssage_edit_text_chat_a);
        SendMessageButton=findViewById(R.id.Send_button_image_chat_a);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();
        PMessage=FirebaseDatabase.getInstance().getReference().child("Messages");
        RootRef=FirebaseDatabase.getInstance().getReference();
        adaptor=new MessageAdaptor(list,this);
        manager=new LinearLayoutManager(this);
        mRecyclerView=findViewById(R.id.mrecyc_view);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adaptor);


    }


}
