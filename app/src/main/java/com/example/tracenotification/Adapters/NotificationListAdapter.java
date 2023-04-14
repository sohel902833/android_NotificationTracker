package com.example.tracenotification.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tracenotification.Model.NotificationModel;

import java.util.List;
import com.example.tracenotification.R;


public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder>{

    private Context context;
    private List<NotificationModel> dataList;
    private  OnItemClickListner listner;

    public NotificationListAdapter(Context context, List<NotificationModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.notification_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       NotificationModel item=dataList.get(position);

       holder.applicationNameTv.setText(""+item.getApplicationName());
       holder.titleTv.setText(""+item.getTitle());
       holder.messageTv.setText(""+item.getBody());
       holder.timeTv.setText(""+item.getDate());

       if(item.getImaBitmap()!=null){

          holder.applicationImageView.setImageBitmap(item.getImaBitmap());
       }

       holder.deleteButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(listner!=null){
                   listner.onDelete(holder.getAdapterPosition(),item);
               }
           }
       });


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        TextView applicationNameTv,messageTv,titleTv,timeTv;
        ImageView applicationImageView;
        Button deleteButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv=itemView.findViewById(R.id.ni_titleTv);
            timeTv=itemView.findViewById(R.id.ni_timeTv);
            applicationNameTv=itemView.findViewById(R.id.ni_applicationNameTv);
            messageTv=itemView.findViewById(R.id.ni_messageTv);
            applicationImageView=itemView.findViewById(R.id.ni_applicationImageId);
            deleteButton=itemView.findViewById(R.id.deleteNotificationBtn);

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
        void onDelete(int position,NotificationModel notification);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
