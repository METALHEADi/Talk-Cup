package com.example.mohamed.clonewhatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {
private android.support.v7.widget.Toolbar toolbar;
private RecyclerView mRecyclerView;
private DatabaseReference mRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        InitializeField();
    }

    private void InitializeField() {

        mRecyclerView=findViewById(R.id.mRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar=findViewById(R.id.friendfindpagelayout);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
        mRoot= FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> contactsFirebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(mRoot,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(contactsFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.UserImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id=getRef(position).getKey();
                        SendUserToProfile(visit_user_id);

                    }
                });


            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
            FindFriendViewHolder holder=new FindFriendViewHolder(view);
            return holder;
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void SendUserToProfile(String visit_user_id) {
    Intent intent=new Intent(FindFriendActivity.this,UserProfileActivity.class);
    intent.putExtra("visit_user_id",visit_user_id);
    startActivity(intent);

    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{
        TextView userName ,userStatus;
        CircleImageView UserImage;


        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_name_display);
            userStatus=itemView.findViewById(R.id.user_status_display);
            UserImage=itemView.findViewById(R.id.user_profile_image);
        }


    }
}
