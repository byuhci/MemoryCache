package com.example.jipark.memorycache.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jipark.memorycache.R;
import com.example.jipark.memorycache.models.Memory;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jipark on 3/11/18.
 */

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {
    private List<Memory> mMemories;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mMemoryImage;
        public TextView mMemoryDescription;
        public ViewHolder(View view) {
            super(view);
            mMemoryImage = view.findViewById(R.id.adapter_image);
            mMemoryDescription = view.findViewById(R.id.adapter_description);
        }
    }

    public MemoryAdapter(List<Memory> memoryList) {
        mMemories = memoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memory_list_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Memory memory = mMemories.get(position);

        Context context = holder.mMemoryImage.getContext();
        Picasso.with(context).load(memory.getImageLink()).into(holder.mMemoryImage);
        holder.mMemoryDescription.setText(memory.getDescription());
    }

    @Override
    public int getItemCount() {
        return mMemories.size();
    }
}
