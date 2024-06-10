package com.example.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.activity.MainActivity;
import com.example.music.activity.PlayMusicActivity;
import com.example.music.adapter.SongAdapter;
import com.example.music.constant.Constant;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.ActivityMainBinding;
import com.example.music.databinding.FragmentArtistListSongBinding;
import com.example.music.model.Song;
import com.example.music.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArtistListSongFragment extends Fragment {
    private FragmentArtistListSongBinding mFragmentArtistListSongBinding;
    private List<Song>  mListSong;
    ActivityMainBinding mActivityMainBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentArtistListSongBinding = FragmentArtistListSongBinding.inflate(inflater,container,false);
        getListSongByArtist();
        initListener();
        return mFragmentArtistListSongBinding.getRoot();
    }

    private void getListSongByArtist() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    if (Objects.equals(song.getArtist(), ArtistFragment.selectedArtist)){
                        mListSong.add(0, song);
                    }

                }
                initUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void initUI() {
        if (getActivity() == null) {
            return;
        }
        mFragmentArtistListSongBinding.playlistname.setText(ArtistFragment.selectedArtist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentArtistListSongBinding.recyclerPlaylist.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        mFragmentArtistListSongBinding.recyclerPlaylist.setAdapter(songAdapter);
        if (mListSong.isEmpty()) mFragmentArtistListSongBinding.playallartist.setVisibility(View.GONE);
    }

    private void goToSongDetail(Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

    private void initListener() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null || activity.getActivityMainBinding() == null) {
            return;
        }
        mFragmentArtistListSongBinding.playallartist.setOnClickListener(v -> {
            MusicService.clearListSongPlaying();
            MusicService.mListSongPlaying.addAll(mListSong);
            MusicService.isPlaying = false;
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });
    }
}
