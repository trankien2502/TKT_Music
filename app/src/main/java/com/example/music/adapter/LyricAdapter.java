package com.example.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.databinding.ItemLyricBinding;

import java.util.List;

public class LyricAdapter extends RecyclerView.Adapter<LyricAdapter.LyricViewHolder>{
    List<String> mListLyric;
    public LyricAdapter(List<String> mListLyric) {
        this.mListLyric = mListLyric;
    }

    @NonNull
    @Override
    public LyricViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLyricBinding itemLyricBinding = ItemLyricBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new LyricViewHolder(itemLyricBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LyricViewHolder holder, int position) {
        String lyric = mListLyric.get(position);
        if (lyric==null) return;
        holder.itemLyricBinding.tvLyrics.setText(lyric);
    }

    @Override
    public int getItemCount() {
        if (mListLyric!=null) return mListLyric.size();
        return 0;
    }

    public static class LyricViewHolder extends RecyclerView.ViewHolder {
        private final ItemLyricBinding itemLyricBinding;
        public LyricViewHolder(ItemLyricBinding itemLyricBinding) {
            super(itemLyricBinding.getRoot());
            this.itemLyricBinding = itemLyricBinding;
        }
    }
}
