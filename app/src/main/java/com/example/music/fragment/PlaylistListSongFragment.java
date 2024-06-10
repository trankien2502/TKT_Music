package com.example.music.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.music.activity.MainActivity;
import com.example.music.activity.PlayMusicActivity;
import com.example.music.adapter.PlaylistGridAdapter;
import com.example.music.adapter.SongAdapter;
import com.example.music.constant.Constant;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.FragmentPlaylistListSongBinding;
import com.example.music.model.Playlist;
import com.example.music.model.Playlist_Song;
import com.example.music.model.Song;
import com.example.music.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlaylistListSongFragment extends Fragment {
    FragmentPlaylistListSongBinding mFragmentPlaylistListSongBinding;

    private List<Song> mListSong  =new ArrayList<>();;
    SongAdapter songAdapter;
    private boolean isEditMode = false;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaylistListSongBinding = FragmentPlaylistListSongBinding.inflate(inflater,container,false);
        getListSongByPlaylistId();
        initListener();
        return mFragmentPlaylistListSongBinding.getRoot();
    }


    private void getListSongByPlaylistId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        String getIdCurrent = sharedPreferences.getString("loginId", String.valueOf(Context.MODE_PRIVATE));
        int idCurrent = Integer.parseInt(getIdCurrent);
        String pathUserId,pathPlaylistId;
        DatabaseReference reference;

        if (PlaylistFragment.idPlaylistSelected ==0){
            pathUserId = String.valueOf(idCurrent);
            reference = firebaseDatabase.getReference("user/"+pathUserId+"/list_favourite");
        }
        else{
            pathPlaylistId = String.valueOf(PlaylistFragment.idPlaylistSelected);
            reference = firebaseDatabase.getReference("playlist/"+pathPlaylistId+"/list");
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSong.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    mListSong.add(song);
                }
                initUI();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initListener() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null || activity.getActivityMainBinding() == null) {
            return;
        }

        if (isEditMode){
            mFragmentPlaylistListSongBinding.tvEditPlaylist.setOnClickListener(v -> {
                isEditMode = false;
                initAdapter();
            });
        } else {
            mFragmentPlaylistListSongBinding.tvEditPlaylist.setOnClickListener(v -> {
                isEditMode = true;
                initAdapter();
            });

        }
        mFragmentPlaylistListSongBinding.playallList.setOnClickListener(v -> {
            MusicService.clearListSongPlaying();
            MusicService.mListSongPlaying.addAll(mListSong);
            MusicService.isPlaying = false;
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });
    }
    private void initUI() {
        if (getActivity() == null) {
            return;
        }
        if(PlaylistFragment.idPlaylistSelected==0){
            mFragmentPlaylistListSongBinding.tvEditPlaylist.setVisibility(View.GONE);

        }
        if (mListSong.size()!=0) mFragmentPlaylistListSongBinding.playallList.setVisibility(View.VISIBLE);
        mFragmentPlaylistListSongBinding.playlistname.setText(PlaylistFragment.namePlaylistSelected);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentPlaylistListSongBinding.recyclerPlaylist.setLayoutManager(linearLayoutManager);

        initAdapter();
    }
    private void initAdapter(){
        if (isEditMode){
            mFragmentPlaylistListSongBinding.tvThongBaoPlaylist.setVisibility(View.VISIBLE);
            songAdapter = new SongAdapter(mListSong, this::goToEditSong);
            mFragmentPlaylistListSongBinding.tvEditPlaylist.setText("XONG");
            mFragmentPlaylistListSongBinding.playallList.setVisibility(View.GONE);
        } else {
            mFragmentPlaylistListSongBinding.tvThongBaoPlaylist.setVisibility(View.GONE);
            songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
            mFragmentPlaylistListSongBinding.tvEditPlaylist.setText("Chỉnh sửa");
            if (mListSong.size()!=0) mFragmentPlaylistListSongBinding.playallList.setVisibility(View.VISIBLE);
        }
        mFragmentPlaylistListSongBinding.recyclerPlaylist.setAdapter(songAdapter);
        initListener();

    }

    private void goToEditSong(Song song) {

        AlertDialog Dialog = new AlertDialog.Builder(getContext())
                .setTitle("GỠ BÀI HÁT")
                .setMessage("Bạn có chắc chắn muốn gỡ "+song.getTitle()+" khỏi playlist "+PlaylistFragment.namePlaylistSelected+"?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeSong(song);
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

    private void removeSong(Song song) {
        String pathSong = String.valueOf(song.getId());
        String pathPlaylistId = String.valueOf(PlaylistFragment.idPlaylistSelected);
        DatabaseReference reference = firebaseDatabase.getReference("playlist/"+pathPlaylistId+"/list/"+pathSong);
        reference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getContext(), "Đã gỡ "+song.getTitle()+"khỏi playlist "+PlaylistFragment.namePlaylistSelected, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToSongDetail(Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }
}
