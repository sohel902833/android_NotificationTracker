package com.example.tracenotification;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.tracenotification.R;
import com.example.tracenotification.Adapters.UserListAdapter;
import com.example.tracenotification.Lib.ApiRef;
import com.example.tracenotification.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentApprovedUser extends Fragment {

    public FragmentApprovedUser() {
        // Required empty public constructor
    }
    private RecyclerView recyclerView;
    private List<User> userList=new ArrayList<>();
    private ProgressDialog progressDialog;
    private UserListAdapter userListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_approved_user, container, false);
        init(view);

        userListAdapter=new UserListAdapter(getContext(),userList);
        recyclerView.setAdapter(userListAdapter);


        userListAdapter.setOnItemClickListner(new UserListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onPending(int position, User user) {
                updateUser(user.getUserId(),User.PENDING_STATUS);
            }

            @Override
            public void onApprove(int position, User user) {
                updateUser(user.getUserId(),User.ACTIVE_STATUS);
            }
        });


        return view;
    }
    private void init(View view) {
        recyclerView=view.findViewById(R.id.recyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog=new ProgressDialog(getContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        Query query = ApiRef.userRef
                .orderByChild("status")
                .equalTo(User.ACTIVE_STATUS);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userList.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User user=snapshot.getValue(User.class);
                        userList.add(user);
                        userListAdapter.notifyDataSetChanged();
                    }
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }

    private  void updateUser(String userId,String status){
        progressDialog.setMessage("Updating.");
        progressDialog.show();
        HashMap<String,Object> updateMap=new HashMap<>();
        updateMap.put("status",status);
        ApiRef.userRef.child(userId)
                .updateChildren(updateMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            onStart();
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "User Updated.", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}