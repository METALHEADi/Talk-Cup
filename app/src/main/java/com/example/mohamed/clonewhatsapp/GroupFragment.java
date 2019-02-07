package com.example.mohamed.clonewhatsapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
private ListView GroupsList;
private View GroupFragmentView;
private ArrayAdapter<String> arrayAdapter;
private ArrayList<String> arrayListGroup;
private DatabaseReference groupRef;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GroupFragmentView= inflater.inflate(R.layout.fragment_group, container, false);
        InitializeFields();
        RetrieveNDisplayGroups();
        WhenU_clickOnItem();








        return GroupFragmentView;
    }

    private void WhenU_clickOnItem() {
    GroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Intent intent =new Intent(getContext(),GroupChatActivity.class);
            intent.putExtra("NameOfGroupChatRoom",arrayListGroup.get(position));
            startActivity(intent);
        }
    });

    }

    private void SentToGroupChatActivity(int position) {
    Intent intent =new Intent(getContext(),GroupChatActivity.class);
    intent.putExtra("NameOfGroupChatRoom",arrayListGroup.get(position));
    startActivity(intent);

    }

    private void RetrieveNDisplayGroups() {
    groupRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Set<String> set=new HashSet<>();
            Iterator iterator=  dataSnapshot.getChildren().iterator();
            while (iterator.hasNext()){
                set.add(((DataSnapshot)iterator.next()).getKey());
                }
                arrayListGroup.clear();
                arrayListGroup.addAll(set);
                arrayAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    private void InitializeFields() {
        arrayListGroup= new ArrayList<>();
        GroupsList=GroupFragmentView.findViewById(R.id.groupList);
        arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,arrayListGroup);
        GroupsList.setAdapter(arrayAdapter);
        groupRef= FirebaseDatabase.getInstance().getReference().child("Groups");
    }


}
