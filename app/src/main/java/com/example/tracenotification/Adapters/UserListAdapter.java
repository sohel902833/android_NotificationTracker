package com.example.tracenotification.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tracenotification.Model.User;
import com.example.tracenotification.R;

import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder>{

    private Context context;
    private List<User> dataList;
    private  OnItemClickListner listner;

    public UserListAdapter(Context context, List<User> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.user_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       User item=dataList.get(position);
       holder.emailTv.setText(""+item.getEmail()+"\n"+"Pass: "+item.getPassword());
       if(item.getStatus().equals(User.PENDING_STATUS)){
           holder.declineButton.setVisibility(View.GONE);
       }else{
           holder.approveButton.setVisibility(View.GONE);
       }
        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onApprove(holder.getAdapterPosition(),item);
                }
            }
        });
        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onPending(holder.getAdapterPosition(),item);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView emailTv;
        Button approveButton,declineButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            emailTv=itemView.findViewById(R.id.user_emailTv);
            approveButton=itemView.findViewById(R.id.user_approveButton);
            declineButton=itemView.findViewById(R.id.user_pendingButton);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listner!=null){
                int position=getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    listner.onItemClick(position);
                }
            }
        }

    }
    public interface  OnItemClickListner{
        void onItemClick(int position);
        void onPending(int position,User user);
        void onApprove(int position,User user);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
