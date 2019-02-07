package com.example.mohamed.clonewhatsapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GMessageViewHolder> {
    private List<GroupChatMessage> messageList;
    private DatabaseReference UserRef,GMessageRef;
    private FirebaseAuth mAuth;
    private  String CurrentUserID;
    private String fromUserId;
    private String GroupChatName;

    public GroupChatAdapter(List<GroupChatMessage> messageList, String groupChatName) {
        this.messageList = messageList;
        GroupChatName = groupChatName;
    }




    @NonNull
    @Override
    public GroupChatAdapter.GMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_msgs_layout,viewGroup,false);
        mAuth=FirebaseAuth.getInstance();



        return new GroupChatAdapter.GMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupChatAdapter.GMessageViewHolder messageViewHolder, int i) {

        CurrentUserID=mAuth.getCurrentUser().getUid();
        GMessageRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupChatName);
        String MessageKEy=GMessageRef.push().getKey();
        GroupChatMessage message=messageList.get(i);
        fromUserId=message.getFrom();
//        fromMessageType=message.getType();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Image")){
                        String recieverImage=dataSnapshot.child("Image").getValue().toString();
                        Log.d("TAG",recieverImage);
                        Picasso.get().load(recieverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.ProfilePic);

                    }
                    else {
                        Picasso.get().load("def").placeholder(R.drawable.profile_image).into(messageViewHolder.ProfilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            messageViewHolder.recieverMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.ProfilePic.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(CurrentUserID)){
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_message);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(message.getMessage());
            }else{
                messageViewHolder.ProfilePic.setVisibility(View.VISIBLE);
                messageViewHolder.recieverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.recieverMessageText.setBackgroundResource(R.drawable.recieve_message);
                messageViewHolder.recieverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.recieverMessageText.setText(message.getMessage());

            }



    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class GMessageViewHolder extends RecyclerView.ViewHolder{
        private TextView senderMessageText,recieverMessageText;
        private CircleImageView ProfilePic;
        public GMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText=itemView.findViewById(R.id.sender_message_tv);
            recieverMessageText=itemView.findViewById(R.id.reciever_message_tv);
            ProfilePic=itemView.findViewById(R.id.messgae_user_image);

        }
    }
}
