package com.example.deardiary;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridListAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<GridListItem> items;

    public GridListAdapter(Context context, ArrayList<GridListItem> items) {
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());

        View itemView=inflater.inflate(R.layout.grid_list_item,parent,false);

        VH holder=new VH(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VH vh= (VH) holder;

        GridListItem item= items.get(position);
        vh.dateInfo.setText(item.getDate());
        Glide.with(holder.itemView.getContext()).load(item.getImgPath()).override(200, 190).into(vh.thumbnail);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder{

        RoundImageView  thumbnail;
        TextView dateInfo;

        public VH(@NonNull View itemView) {
            super(itemView);

            thumbnail=itemView.findViewById(R.id.iv_post);
            thumbnail.setRectRadius(20f);
            dateInfo=itemView.findViewById(R.id.date_text);


        }
    }
}












