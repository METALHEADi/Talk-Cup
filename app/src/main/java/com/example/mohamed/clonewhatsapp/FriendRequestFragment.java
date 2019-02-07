package com.example.mohamed.clonewhatsapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends Fragment {
private  View mFriendRequest;
private DatabaseReference mChatRqRef,UserRef,ContactRef,ChatReq;
private FirebaseAuth mAuth;
private RecyclerView mRecyclerView;
private String CurrentUserID;



    public FriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mFriendRequest= inflater.inflate(R.layout.fragment_friend_request, container, false);
        InitializeField();

        return mFriendRequest;
    }

    private void InitializeField() {
mRecyclerView=mFriendRequest.findViewById(R.id.myrequest_fragment);
mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
mChatRqRef=FirebaseDatabase.getInstance().getReference().child("ChatRequest");
mAuth=FirebaseAuth.getInstance();
CurrentUserID=mAuth.getCurrentUser().getUid();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        ContactRef=FirebaseDatabase.getInstance().getReference().child("UserContacts");
        ChatReq=FirebaseDatabase.getInstance().getReference().child("ChatRequest");

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> firebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(mChatRqRef.child(CurrentUserID),Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,FriendReqViewHolder> adapter =new FirebaseRecyclerAdapter<Contacts, FriendReqViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendReqViewHolder holder, int position, @NonNull Contacts model) {
                final String userId=getRef(position).getKey();
                DatabaseReference gettype=mChatRqRef.child(userId).child(CurrentUserID).child("request_type").getRef();
                DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if (dataSnapshot.exists()){
                       String type=dataSnapshot.getValue().toString();
                       if (type.equals("Recieved")){
                    UserRef.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("Name") &&dataSnapshot.hasChild("Status")){
                                String Name= dataSnapshot.child("Name").getValue().toString();
                                String Status=dataSnapshot.child("Status").getValue().toString();
                                holder.userName.setText(Name);
                                holder.userStatus.setText(Status);
                                if (dataSnapshot.hasChild("Image")){
                                    String Image=dataSnapshot.child("Image").getValue().toString();
                                    Picasso.get().load(Image).placeholder(R.drawable.profile_image).into(holder.UserImage);

                                }



                            }
                            holder.Yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                   AcceptREq(userId);
                                }
                            });
                            holder.No.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CancelChatRequest(userId);
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                       }if (type.equals("sent")){

                           UserRef.child(userId).addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.hasChild("Name") &&dataSnapshot.hasChild("Status")){
                                       holder.itemView.findViewById(R.id.yes_bt).setVisibility(View.INVISIBLE);
                                       holder.itemView.findViewById(R.id.no_bt).setVisibility(View.INVISIBLE);

                                       String Name= dataSnapshot.child("Name").getValue().toString();
                                       String Status="You Sent a Friend Request \n , Please Wait a response";
                                       holder.userName.setText(Name);
                                       holder.userStatus.setText(Status);
                                       if (dataSnapshot.hasChild("Image")){
                                           String Image=dataSnapshot.child("Image").getValue().toString();
                                           Picasso.get().load(Image).placeholder(R.drawable.profile_image).into(holder.UserImage);

                                       }



                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });
                       }

                   }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






            }

            @NonNull
            @Override
            public FriendReqViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_request_layout,viewGroup,false);
                FriendReqViewHolder holder=new FriendReqViewHolder(view);

                return holder;
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void AcceptREq(final String user_id) {
        ContactRef.child(CurrentUserID).child(user_id).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ContactRef.child(user_id).child(CurrentUserID).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                ChatReq.child(CurrentUserID).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            ChatReq.child(user_id).child(CurrentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    SendMessage.setText("Remove this Contact");
//                                                    CurrentState="friends";
//                                                    SendMessage.setEnabled(true);
//                                                    CancelRequestBT.setVisibility(View.INVISIBLE);
//                                                    CancelRequestBT.setEnabled(false);
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
    private void CancelChatRequest(final String user_id1) {
        ChatReq.child(CurrentUserID).child(user_id1).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    ChatReq.child(user_id1).child(CurrentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                            SendMessage.setText("Send Message");
//                            CurrentState="new";
//                            SendMessage.setEnabled(true);
//                            CancelRequestBT.setVisibility(View.INVISIBLE);
//                            CancelRequestBT.setEnabled(false);
                        }

                    });
                }
            }
        });

    }

    public static class FriendReqViewHolder extends RecyclerView.ViewHolder{
        TextView userName ,userStatus;
        CircleImageView UserImage;
        ImageButton Yes,No;


        public FriendReqViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_name_display1);
            userStatus=itemView.findViewById(R.id.user_status_display1);
            UserImage=itemView.findViewById(R.id.user_profile_image1);
            Yes=itemView.findViewById(R.id.yes_bt);
            No=itemView.findViewById(R.id.no_bt);

        }


    }
}
