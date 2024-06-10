package com.example.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;
import com.example.music.databinding.ItemPlaylistGridBinding;
import com.example.music.listener.IOnClickPlaylistItemListener;
import com.example.music.model.Playlist;
import com.example.music.utils.GlideUtils;

import java.util.List;

public class PlaylistGridAdapter extends RecyclerView.Adapter<PlaylistGridAdapter.PlaylistGridViewHolder> {

    private final List<Playlist> mListPlaylist;
    public final IOnClickPlaylistItemListener iOnClickPlaylistItemListener;

    public PlaylistGridAdapter(List<Playlist> mListPlaylist, IOnClickPlaylistItemListener iOnClickPlaylistItemListener) {
        this.mListPlaylist = mListPlaylist;
        this.iOnClickPlaylistItemListener = iOnClickPlaylistItemListener;
    }

    @NonNull
    @Override
    public PlaylistGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistGridBinding itemPlaylistGridBinding = ItemPlaylistGridBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PlaylistGridViewHolder(itemPlaylistGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistGridViewHolder holder, int position) {
        Playlist playlist = mListPlaylist.get(position);
        if (playlist == null) {
            return;
        }
        //GlideUtils.loadUrl(playlist.getImage(), holder.mItemSongGridBinding.imgSong);
        holder.mItemPlaylistGridBinding.tvPlaylist.setText(playlist.getName());
        holder.mItemPlaylistGridBinding.imagePlaylist.setImageResource(R.drawable.ic_playlist);
        holder.mItemPlaylistGridBinding.layoutItemPlaylist.setOnClickListener(v -> iOnClickPlaylistItemListener.onClickPlaylistItemListener(playlist));
        //holder.mItemPlaylistGridBinding.layoutItemPlaylist.setOnLongClickListener(v -> iOnClickPlaylistItemListener.OnLongClickPlaylistItemListener(playlist));
    }

    @Override
    public int getItemCount() {
        if (mListPlaylist != null){
            return  mListPlaylist.size();
        }
        return 0;
    }

    public static class PlaylistGridViewHolder extends RecyclerView.ViewHolder{
        private final ItemPlaylistGridBinding mItemPlaylistGridBinding;
        public PlaylistGridViewHolder(ItemPlaylistGridBinding itemPlaylistGridBinding) {
            super(itemPlaylistGridBinding.getRoot());
            this.mItemPlaylistGridBinding = itemPlaylistGridBinding;
        }
    }
}
