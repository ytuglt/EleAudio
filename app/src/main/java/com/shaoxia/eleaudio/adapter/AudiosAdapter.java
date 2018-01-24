package com.shaoxia.eleaudio.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shaoxia.eleaudio.R;
import com.shaoxia.eleaudio.model.MDevice;

import java.io.File;
import java.util.List;

/**
 * Created by gonglt1 on 18-1-11.
 */

public class AudiosAdapter extends RecyclerView.Adapter<AudiosAdapter.MyViewHolder> implements View.OnClickListener {

    private List<File> dataList;
    private OnItemClickListener onItemClickListener;


    public AudiosAdapter(List<File> list) {
        dataList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_aduio_layout, null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.itemView.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        String title = dataList.get(position).getName();
        holder.textView.setText(title);
//        holder.tVId.setText(mDev.getElevatorId());
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
