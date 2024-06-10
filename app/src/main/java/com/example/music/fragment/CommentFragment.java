package com.example.music.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.activity.MainActivity;
import com.example.music.activity.PlayMusicActivity;
import com.example.music.adapter.CommentAdapter;
import com.example.music.adapter.SongAdapter;
import com.example.music.constant.Constant;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.FragmentCommentBinding;
import com.example.music.databinding.FragmentNewSongsBinding;
import com.example.music.model.Comment;
import com.example.music.model.Song;
import com.example.music.model.User;
import com.example.music.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    private FragmentCommentBinding mFragmentCommentBinding;
    private List<Comment> mListComment = new ArrayList<>();
    private List<Comment> mListOwnerComment = new ArrayList<>();

    private int idCommentMax=0;
    private String username = "Unknown";


    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateListCommentSongPlaying();
        }
    };
    CommentAdapter commentAdapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentCommentBinding = FragmentCommentBinding.inflate(inflater, container, false);
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }


        displayListComment();
        initListener();

        return mFragmentCommentBinding.getRoot();
    }

    private void updateListCommentSongPlaying() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        Boolean getCheck = sharedPreferences.getBoolean("loginCheck", Boolean.parseBoolean(String.valueOf(Context.MODE_PRIVATE)));
        String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
        int idCurrent;
        if (!getIdCurrent.equals("")){
            idCurrent = Integer.parseInt(getIdCurrent);
        } else {
            idCurrent = -1;
        }

        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        DatabaseReference reference = firebaseDatabase.getReference("comment");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListComment = new ArrayList<>();
                mListOwnerComment = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    if (comment == null) {
                        return;
                    }
                    if (comment.getSongid()==currentSong.getId()){
                        if (!getCheck){
                            comment.setOwnercomment(false);
                        }
                        else {
                            if(comment.getUserid() == idCurrent){

                                comment.setOwnercomment(true);
                                comment.setUsername(username);
                                mListOwnerComment.add(comment);
                            }
                            else {
                                comment.setOwnercomment(false);
                            }

                        }
                        mListComment.add(comment);
                    }
                    idCommentMax = comment.getId();
                }
                CommentAdapter commentAdapter = new CommentAdapter(mListComment, this::deleteComment);
                mFragmentCommentBinding.rcvComment.setAdapter(commentAdapter);
            }

            private void deleteComment(Comment comment) {
                String pathCmt = String.valueOf(comment.getId());
                DatabaseReference reference = firebaseDatabase.getReference("comment/"+pathCmt);
                AlertDialog Dialog = new AlertDialog.Builder(getContext())
                        .setTitle("XÓA BÌNH LUẬN")
                        .setMessage("Bạn có chắc chắn muốn xóa bình luận: "+comment.getComment()+"?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference.removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(getContext(),"Delete Comment Success!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create();
                Dialog.show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });

    }

    private void displayListComment() {
        if (getActivity() == null) {
            return;
        }
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        Boolean getCheck = sharedPreferences.getBoolean("loginCheck", Boolean.valueOf(String.valueOf(Context.MODE_PRIVATE)));
        if (getCheck) {
            getUsernameCurrent();
            mFragmentCommentBinding.layoutUpCmt.setVisibility(View.VISIBLE);
        } else {
            mFragmentCommentBinding.layoutUpCmt.setVisibility(View.GONE);
            mFragmentCommentBinding.tvMyComment.setVisibility(View.GONE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentCommentBinding.rcvComment.setLayoutManager(linearLayoutManager);

        commentAdapter = new CommentAdapter(mListComment, this::deleteComment);
        mFragmentCommentBinding.rcvComment.setAdapter(commentAdapter);

        updateListCommentSongPlaying();
    }



    private void initListener() {
        mFragmentCommentBinding.btnAddComment.setOnClickListener(v -> addNewComment());
        mFragmentCommentBinding.tvMyComment.setOnClickListener(v -> showMyComment());
        mFragmentCommentBinding.tvCmt.setOnClickListener(v -> showComment());
    }
    private void getUsernameCurrent(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
        int idCurrent = Integer.parseInt(getIdCurrent);
        DatabaseReference reference = firebaseDatabase.getReference("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
                        return;
                    }
                    if (user.getId()==idCurrent){
                        if (user.getName()==null) username = user.getEmail();
                        else username = user.getName();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }
    private void showComment() {
        CommentAdapter commentAdapter = new CommentAdapter(mListComment, this::deleteComment);
        mFragmentCommentBinding.rcvComment.setAdapter(commentAdapter);
    }
    private void showMyComment() {
        CommentAdapter commentAdapter = new CommentAdapter(mListOwnerComment, this::deleteComment);
        mFragmentCommentBinding.rcvComment.setAdapter(commentAdapter);
    }

    private void addNewComment() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));

        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        int idCurrent = Integer.parseInt(getIdCurrent);
        String typingComment = mFragmentCommentBinding.tvAddComment.getText().toString().trim();
        Comment comment = new Comment(idCommentMax+1,currentSong.getId(),idCurrent,username,typingComment);
        String pathCmt = String.valueOf(idCommentMax+1);

        DatabaseReference reference = firebaseDatabase.getReference("comment");
        reference.child(pathCmt).setValue(comment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getContext(),"Push Comment Success!",Toast.LENGTH_SHORT).show();
                mFragmentCommentBinding.tvAddComment.setText("");
            }
        });
    }
    private void deleteComment(Comment comment) {
        String pathCmt = String.valueOf(comment.getId());
        DatabaseReference reference = firebaseDatabase.getReference("comment/"+pathCmt);
        AlertDialog Dialog = new AlertDialog.Builder(getContext())
                .setTitle("XÓA BÌNH LUẬN")
                .setMessage("Bạn có chắc chắn muốn xóa bình luận này?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getContext(),"Delete Comment Success!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        Dialog.show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
    }
}
