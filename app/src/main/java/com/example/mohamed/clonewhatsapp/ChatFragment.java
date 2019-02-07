package com.example.mohamed.clonewhatsapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
public class ChatFragment extends Fragment {
private RecyclerView mReceylerView ;
private DatabaseReference ContactRef,UserRef,PChatRef;
private FirebaseAuth mAuth;
private String CurrentUserId;
private View myView;



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView=inflater.inflate(R.layout.fragment_chart, container, false);
        initializeFields();


        return myView;
    }

    private void initializeFields() {
    mReceylerView=myView.findViewById(R.id.mrecycler_view_chat_fragment);
    mReceylerView.setLayoutManager(new LinearLayoutManager(getContext()));
    UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
    ContactRef=FirebaseDatabase.getInstance().getReference().child("UserContacts");
    mAuth=FirebaseAuth.getInstance();
    CurrentUserId=mAuth.getCurrentUser().getUid();
    PChatRef=FirebaseDatabase.getInstance().getReference().child("Messages");


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> firebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(PChatRef.child(CurrentUserId),Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,ChatFragmentViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ChatFragmentViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatFragmentViewHolder holder, int position, @NonNull Contacts model) {
                final String UserID=getRef(position).getKey();
                UserRef.child(UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String[] Image_ST={"def"};
                        if (dataSnapshot.hasChild("Name") &&dataSnapshot.hasChild("Status")){

                            final String Name= dataSnapshot.child("Name").getValue().toString();
                            String Status=dataSnapshot.child("Status").getValue().toString();
                            String state=dataSnapshot.child("UserState").child("state").getValue().toString();
                            String time=dataSnapshot.child("UserState").child("time").getValue().toString();
                            String date=dataSnapshot.child("UserState").child("date").getValue().toString();
                            holder.UserName.setText(Name);
                            if (state.equals("online")){
                            holder.USerStatus.setText(Status);
                            holder.userProfileState.setVisibility(View.VISIBLE);

                            }else if (state.equals("offline")){
                                holder.USerStatus.setText("Last Seen: "+time+" "+date);
                                holder.userProfileState.setVisibility(View.INVISIBLE);
                            }
                            if (dataSnapshot.hasChild("Image")){
                                Image_ST[0]=dataSnapshot.child("Image").getValue().toString();
                                Picasso.get().load(Image_ST[0]).placeholder(R.drawable.profile_image).into(holder.UserImage);



                            }
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendToChatActivity(UserID,Name,Image_ST[0]);
                                }
                            });



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            }


            @NonNull
            @Override
            public ChatFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
            ChatFragmentViewHolder holder=new ChatFragmentViewHolder(view);
                return holder;
            }
        };


        mReceylerView.setAdapter(adapter);
        adapter.startListening();

    }
    public static class ChatFragmentViewHolder extends RecyclerView.ViewHolder{
private CircleImageView UserImage;
private TextView UserName,USerStatus;
private ImageView userProfileState;


        public ChatFragmentViewHolder(@NonNull View itemView) {
            super(itemView);
           UserImage=itemView.findViewById(R.id.user_profile_image);
            UserName=itemView.findViewById(R.id.user_name_display);
            USerStatus=itemView.findViewById(R.id.user_status_display);
            userProfileState=itemView.findViewById(R.id.online_offline);

        }




    }
    private void SendToChatActivity(String UserID, String UserName, String Image) {
        Intent intent=new Intent(getContext(),ChatActivity.class);
        intent.putExtra("UserID",UserID);
        intent.putExtra("UserName",UserName);
        intent.putExtra("UserImage",Image);

        startActivity(intent);

    }
}
