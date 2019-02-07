package com.example.mohamed.clonewhatsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
private CircleImageView VisitUserImage;
private TextView visitUserName,visitUserStatus;
private Button SendMessage,CancelRequestBT;
private DatabaseReference mRoot,ChatReq,ContactRef,NotificationChatRequestRef;
private String UserUID,CurrentState,CurrentUserID,SenderId,RecieverID;
private FirebaseAuth mAuth;
private FirebaseUser mUSer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        UserUID=getIntent().getExtras().getString("visit_user_id");
        InitializeFields();
        RetrieveUserData();

    }

    private void RetrieveUserData() {
    mRoot.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(UserUID.equals(CurrentUserID)){
                SendMessage.setVisibility(View.INVISIBLE);

            }
            if (dataSnapshot.exists() && dataSnapshot.hasChild("Name")&& dataSnapshot.hasChild("Status")){
              visitUserName.setText(dataSnapshot.child("Name").getValue().toString());
              visitUserStatus.setText(dataSnapshot.child("Status").getValue().toString());
                if (dataSnapshot.hasChild("Image")){
                    Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).into(VisitUserImage);
                }
                ManageChatRequest();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    private void    ManageChatRequest() {
        ChatReq.child(SenderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(RecieverID)){
            String request_type=dataSnapshot.child(RecieverID).child("request_type").getValue().toString();
            if (request_type.equals("sent")) {

                SendMessage.setText("Cancel Chat Request");

            }
            else if (request_type.equals("Recieved")){
            CurrentState="request_recived";
            SendMessage.setText("Accept Chat Request");
            CancelRequestBT.setVisibility(View.VISIBLE);
            CancelRequestBT.setEnabled(true);
            CancelRequestBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CancelChatRequest();
                }
            });

            }


                }
                else {
                    ContactRef.child(SenderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(RecieverID)){

                                CurrentState="friends";
                                SendMessage.setText("Remove this Contact");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage.setEnabled(false);
                if (CurrentState.equals("new")){

                    SendChatRequest();

                }
                if(CurrentState.equals("request_sent")){
                    CancelChatRequest();

                }
                if(CurrentState.equals("request_recived")){

                    AcceptChatRequest();
                }
                if(CurrentState.equals("friends")){

                    RemoveThisContacts();
                }
            }
        });



    }

    private void RemoveThisContacts() {
    ContactRef.child(SenderId).child(RecieverID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){

                ContactRef.child(RecieverID).child(SenderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
SendMessage.setText("Send Message");
CurrentState="new";
SendMessage.setEnabled(true);
CancelRequestBT.setVisibility(View.INVISIBLE);
CancelRequestBT.setEnabled(false);

                        }
                    }
                });
            }
        }
    });


    }

    protected void AcceptChatRequest() {
        ContactRef.child(SenderId).child(RecieverID).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if (task.isSuccessful()){
               ContactRef.child(RecieverID).child(SenderId).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){

                           ChatReq.child(SenderId).child(RecieverID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){

                                       ChatReq.child(RecieverID).child(SenderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               SendMessage.setText("Remove this Contact");
                                               CurrentState="friends";
                                               SendMessage.setEnabled(true);
                                               CancelRequestBT.setVisibility(View.INVISIBLE);
                                               CancelRequestBT.setEnabled(false);
                                           }

                                       });
                                   }
                               }
                           });
                       }
                   }
               });

           }
            }
        });
    }

    private void CancelChatRequest() {
    ChatReq.child(SenderId).child(RecieverID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){

                ChatReq.child(RecieverID).child(SenderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    SendMessage.setText("Send Message");
                    CurrentState="new";
                    SendMessage.setEnabled(true);
                    CancelRequestBT.setVisibility(View.INVISIBLE);
                    CancelRequestBT.setEnabled(false);
                    }

                });
            }
        }
    });

    }

    private void SendChatRequest() {
        ChatReq.child(SenderId).child(RecieverID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if (task.isSuccessful()){
               ChatReq.child(RecieverID).child(SenderId).child("request_type").setValue("Recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           HashMap<String,String> ChatNotification=new HashMap<>();
                           ChatNotification.put("from",SenderId);
                           ChatNotification.put("type","request");
                            NotificationChatRequestRef.child(RecieverID).push().setValue(ChatNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        SendMessage.setEnabled(true);
                                        CurrentState="request_sent";
                                        SendMessage.setText("Cancel Chat Request");
                                    }
                                }
                            });



                   }}
               });
           }
            }
        });



    }

    private void InitializeFields() {
    VisitUserImage=findViewById(R.id.visit_user_iamge);
    visitUserName =findViewById(R.id.visit_user_name);
    visitUserStatus=findViewById(R.id.visit_user_status);
    SendMessage=findViewById(R.id.visit_user_send_message);
    mRoot= FirebaseDatabase.getInstance().getReference().child("Users").child(UserUID);
    CurrentState="new";
    mAuth=FirebaseAuth.getInstance();
    mUSer=mAuth.getCurrentUser();
    CurrentUserID=mAuth.getCurrentUser().getUid();
    ChatReq=FirebaseDatabase.getInstance().getReference().child("ChatRequest");
    SenderId=CurrentUserID;
    RecieverID=UserUID;
    CancelRequestBT=findViewById(R.id.visit_user_cancel_message);
    ContactRef=FirebaseDatabase.getInstance().getReference().child("UserContacts");
    NotificationChatRequestRef=FirebaseDatabase.getInstance().getReference().child("Notification");



    }
}
