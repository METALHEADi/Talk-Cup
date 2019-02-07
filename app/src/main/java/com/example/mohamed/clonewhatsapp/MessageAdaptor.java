package com.example.mohamed.clonewhatsapp;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.MessageViewHolder> {
    private List<Message> messageList;
    private DatabaseReference UserRef,MessageRef;
    private FirebaseAuth mAuth;
    private  String CurrentUserID,fromUserId,fromMessageType;
    private Context mC;

    public MessageAdaptor(List<Message> messageList, Context mContext){
        this.messageList=messageList;
        this.mC=mContext;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_msgs_layout,viewGroup,false);
        mAuth=FirebaseAuth.getInstance();


        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
        CurrentUserID=mAuth.getCurrentUser().getUid();
        MessageRef=FirebaseDatabase.getInstance().getReference().child("Messages");
        Message message=messageList.get(i);
        fromUserId=message.getFrom();
        fromMessageType=message.getType();
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
        if (fromMessageType.equals("text")){
            messageViewHolder.recieverMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.ProfilePic.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(CurrentUserID)){
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_message);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(message.getMessage());
            }else if(!fromUserId.equals(CurrentUserID)){
                messageViewHolder.ProfilePic.setVisibility(View.VISIBLE);
                messageViewHolder.recieverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.recieverMessageText.setBackgroundResource(R.drawable.recieve_message);
                messageViewHolder.recieverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.recieverMessageText.setText(message.getMessage());
//                try
//                {
//
//                    Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+mC.getPackageName() +"/raw/quiteimpressed");
//                    Ringtone r = RingtoneManager.getRingtone(mC, alarmSound);
//                    r.play();
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }

            }

        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView  senderMessageText,recieverMessageText;
        private CircleImageView  ProfilePic;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText=itemView.findViewById(R.id.sender_message_tv);
            recieverMessageText=itemView.findViewById(R.id.reciever_message_tv);
            ProfilePic=itemView.findViewById(R.id.messgae_user_image);

        }
    }
}
