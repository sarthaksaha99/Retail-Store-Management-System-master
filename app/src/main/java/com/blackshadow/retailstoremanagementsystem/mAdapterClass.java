package com.blackshadow.retailstoremanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class mAdapterClass extends  RecyclerView.Adapter<mAdapterClass.MyViewHolder>{

    ArrayList<Users> list;
    private Context context;

    public mAdapterClass(Context context) {
        this.context = context;
    }

    public mAdapterClass(ArrayList<Users> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public mAdapterClass.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_holder,parent,false);

        return new mAdapterClass.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mAdapterClass.MyViewHolder holder, final int position) {
        holder.fnam.setText(list.get(position).getName());
        holder.desc.setText(list.get(position).getPhone());

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView fnam,desc;
        Button button;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fnam=itemView.findViewById(R.id.nameDoc);
            desc=itemView.findViewById(R.id.description);
            button=itemView.findViewById(R.id.btnrv);

            button.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(),mManageActivity.class);
            intent.putExtra("uid",list.get(getAdapterPosition()).getUid());
//            intent.putExtra("name",list.get(getAdapterPosition()).getName());
            Toast.makeText(v.getContext(),list.get(getAdapterPosition()).getUid(),Toast.LENGTH_SHORT).show();
            v.getContext().startActivity(intent);
        }
    }
}

