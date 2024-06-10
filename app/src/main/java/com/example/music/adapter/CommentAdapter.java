package com.example.music.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music.activity.SignInActivity;
import com.example.music.databinding.ItemCommentBinding;
import com.example.music.listener.IOnClickCommentItemListener;
import com.example.music.model.Comment;
import com.example.music.model.User;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<Comment> mListComments;
    public IOnClickCommentItemListener iOnClickCommentItemListener;





    public CommentAdapter(List<Comment> mListComments) {
        this.mListComments = mListComments;
    }
    public CommentAdapter(List<Comment> mListComments, IOnClickCommentItemListener iOnClickCommentItemListener) {
        this.mListComments = mListComments;
        this.iOnClickCommentItemListener = iOnClickCommentItemListener;
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding itemCommentBinding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CommentViewHolder(itemCommentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = mListComments.get(position);
        if (comment==null) return;

        if (comment.isOwnercomment()){
            holder.mItemCommentBinding.tvDelComment.setVisibility(View.VISIBLE);
            holder.mItemCommentBinding.tvDelComment.setOnClickListener(v -> iOnClickCommentItemListener.onClickCommentItemListener(comment));
        }else {
            holder.mItemCommentBinding.tvDelComment.setVisibility(View.GONE);
        }
        holder.mItemCommentBinding.tvUserName.setText(comment.getUsername());
        holder.mItemCommentBinding.tvComment.setText(comment.getComment());

    }

    @Override
    public int getItemCount() {
        if (mListComments!=null) return mListComments.size();
        return 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding mItemCommentBinding;
        public CommentViewHolder(ItemCommentBinding itemCommentBinding) {
            super(itemCommentBinding.getRoot());
            this.mItemCommentBinding = itemCommentBinding;
        }
    }
}
