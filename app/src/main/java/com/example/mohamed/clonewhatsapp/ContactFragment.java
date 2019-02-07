package com.example.mohamed.clonewhatsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
public class ContactFragment extends Fragment {
private RecyclerView mRecycleView;
private DatabaseReference mRoot,UserRef;
private View ContactFragmentView;
private FirebaseAuth mAuth;
private String CurrentUserID;
private String Images;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ContactFragmentView= inflater.inflate(R.layout.fragment_contect, container, false);
        InitializeField();

        return ContactFragmentView;
    }

    private void InitializeField() {
    mRecycleView=ContactFragmentView.findViewById(R.id.contact_fragment_recycle);
    mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
    mAuth=FirebaseAuth.getInstance();
    CurrentUserID=mAuth.getCurrentUser().getUid();
        mRoot= FirebaseDatabase.getInstance().getReference().child("UserContacts").child(CurrentUserID);
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");

    }


    public ContactFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> contactsFirebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(mRoot,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ContactViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Contacts, ContactViewHolder>(contactsFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactViewHolder holder, int position, @NonNull Contacts model) {
                final String usersID=getRef(position).getKey();
                UserRef.child(usersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String[] Image_ST={"def"};
                        if (dataSnapshot.hasChild("Name") &&dataSnapshot.hasChild("Status")){

                            final String Name= dataSnapshot.child("Name").getValue().toString();
                             String Status= dataSnapshot.child("Status").getValue().toString();

                            String statue=dataSnapshot.child("UserState").child("state").getValue().toString();
                            String userStateTime=dataSnapshot.child("UserState").child("time").getValue().toString();
                            String userStateDate=dataSnapshot.child("UserState").child("date").getValue().toString();

                            holder.userName.setText(Name);

                            holder.userStatus.setText(Status);
                            if (dataSnapshot.hasChild("Image")){
                               Image_ST[0]=dataSnapshot.child("Image").getValue().toString();
                                Picasso.get().load(Image_ST[0]).placeholder(R.drawable.profile_image).into(holder.UserImage);



                            }
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendToChatActivity(usersID,Name,Image_ST[0]);
                                }
                            });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                ContactViewHolder holder=new ContactViewHolder(view);
                return holder;
            }
        };
        mRecycleView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }

//    private void SendToChatActivity(String usersID, String name, String[] image_ST) {
//        Intent intent=new Intent(getContext(),ChatActivity.class);
//        intent.putExtra("UserID",usersID);
//        intent.putExtra("UserName",name);
//
//        startActivity(intent);
//
//    }

    private void SendToChatActivity(String UserID, String UserName, String Image) {
        Intent intent=new Intent(getContext(),ChatActivity.class);
        intent.putExtra("UserID",UserID);
        intent.putExtra("UserName",UserName);
        intent.putExtra("UserImage",Image);

        startActivity(intent);

    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        TextView userName ,userStatus;
        CircleImageView UserImage;
        ImageView userOnlineState;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            userOnlineState=itemView.findViewById(R.id.online_offline);
            userName=itemView.findViewById(R.id.user_name_display);
            userStatus=itemView.findViewById(R.id.user_status_display);
            UserImage=itemView.findViewById(R.id.user_profile_image);
        }


    }
}
