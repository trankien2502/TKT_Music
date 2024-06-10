package com.example.music.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.activity.PlayMusicActivity;
import com.example.music.adapter.SongAdapter;
import com.example.music.constant.Constant;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.FragmentSearchSongBinding;
import com.example.music.model.Song;
import com.example.music.service.MusicService;
import com.example.music.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchSongFragment extends Fragment {
    private FragmentSearchSongBinding mFragmentSearchSongBinding;
    private SongAdapter songAdapter;
    private List<Song> mListSongSearch;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentSearchSongBinding = FragmentSearchSongBinding.inflate(inflater,container,false);
        initListener();
        getListSongSearchFromFirebase(HomeFragment.textSearchSong);
        return mFragmentSearchSongBinding.getRoot();
    }

    private void initListener() {
        mFragmentSearchSongBinding.edtFragmentSearchSongName.setText(HomeFragment.textSearchSong);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentSearchSongBinding.rcvFragmentListSearch.setLayoutManager(linearLayoutManager);
        if (getActivity() == null) {
            return;
        }
        mFragmentSearchSongBinding.edtFragmentSearchSongName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                getListSongSearchFromFirebase(strKey);
            }
        });

    }
    private void initAdapter(){
        if (mListSongSearch!=null){
            mFragmentSearchSongBinding.noResultSong.setVisibility(View.GONE);
        }
        if (mListSongSearch.size() == 0) {
            mFragmentSearchSongBinding.noResultSong.setVisibility(View.VISIBLE);
        }
        songAdapter = new SongAdapter(mListSongSearch, this::goToSongDetail);
        mFragmentSearchSongBinding.rcvFragmentListSearch.setAdapter(songAdapter);
    }
    private void getListSongSearchFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSongSearch = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    if (GlobalFuntion.getTextSearch(song.getTitle()).toLowerCase().trim()
                            .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())) {
                        mListSongSearch.add(song);
                    }
                }
                initAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }
    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

}