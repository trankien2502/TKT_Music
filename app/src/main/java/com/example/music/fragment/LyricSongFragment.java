package com.example.music.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.music.R;
import com.example.music.adapter.LyricAdapter;
import com.example.music.constant.Constant;
import com.example.music.databinding.FragmentLyricSongBinding;
import com.example.music.model.Song;
import com.example.music.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LyricSongFragment extends Fragment {
    private FragmentLyricSongBinding mFragmentLyricSongBinding;
    private LyricAdapter mLyricAdapter;
    private List<String> mListLyric ;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://musicbasic-27309-default-rtdb.firebaseio.com");
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatusLyricSongPlaying();
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentLyricSongBinding = FragmentLyricSongBinding.inflate(inflater,container,false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        displayLyric();
        return mFragmentLyricSongBinding.getRoot();
    }

    private void updateStatusLyricSongPlaying() {
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        DatabaseReference reference = firebaseDatabase.getReference("songs/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListLyric = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    if (song.getId()==currentSong.getId()){
                        if(song.getLyric()==null){
                            mFragmentLyricSongBinding.tvNoneLyric.setVisibility(View.VISIBLE);
                            return;
                        }
                        String lyrics = song.getLyric();
                        mListLyric.addAll(splitLyric(lyrics));
                    }
                    //if (mListLyric==null) mFragmentLyricSongBinding.tvNoneLyric.setVisibility(View.VISIBLE);
                    mFragmentLyricSongBinding.tvSongPlayingLyric.setText(currentSong.getTitle());
                    mLyricAdapter = new LyricAdapter(mListLyric);
                    mFragmentLyricSongBinding.rcvLyricSongPlaying.setAdapter(mLyricAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void displayLyric() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentLyricSongBinding.rcvLyricSongPlaying.setLayoutManager(linearLayoutManager);

        mLyricAdapter = new LyricAdapter(mListLyric);
        mFragmentLyricSongBinding.rcvLyricSongPlaying.setAdapter(mLyricAdapter);
        updateStatusLyricSongPlaying();
    }
    private List<String> splitLyric(String lyric){
        String[] arrLyric = lyric.split("\\.");
        return new ArrayList<>(Arrays.asList(arrLyric));
    }
}