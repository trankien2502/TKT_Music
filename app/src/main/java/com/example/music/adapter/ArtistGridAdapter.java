package com.example.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.databinding.ItemArtistGridBinding;
import com.example.music.listener.IOnClickArtistItemListener;
import com.example.music.model.Artist;
import com.example.music.utils.GlideUtils;


import java.util.List;

public class ArtistGridAdapter extends RecyclerView.Adapter<ArtistGridAdapter.ArtistGridViewHolder>{
    private final List<Artist> mListArtists;
    public final IOnClickArtistItemListener iOnClickArtistItemListener;

    public ArtistGridAdapter(List<Artist> mListArtists, IOnClickArtistItemListener iOnClickArtistItemListener){
        this.mListArtists = mListArtists;
        this.iOnClickArtistItemListener = iOnClickArtistItemListener;
    }

    @NonNull
    @Override
    public ArtistGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArtistGridBinding itemArtistGridBinding = ItemArtistGridBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ArtistGridViewHolder(itemArtistGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistGridViewHolder holder, int position) {
        Artist artist = mListArtists.get(position);
        if (artist == null) {
            return;
        }
        GlideUtils.loadUrl(artist.getImage(), holder.mItemArtistGridBinding.imageThumbnail);
        holder.mItemArtistGridBinding.tvArtist.setText(artist.getName());
        holder.mItemArtistGridBinding.layoutItem.setOnClickListener(v -> iOnClickArtistItemListener.onClickArtistItemListener(artist));
    }

    @Override
    public int getItemCount() {
        return null == mListArtists ? 0 : mListArtists.size();
    }

    public static class ArtistGridViewHolder extends RecyclerView.ViewHolder {

        private final ItemArtistGridBinding mItemArtistGridBinding;

        public ArtistGridViewHolder(ItemArtistGridBinding itemArtistGridBinding) {
            super(itemArtistGridBinding.getRoot());
            this.mItemArtistGridBinding = itemArtistGridBinding;
        }
    }
}
