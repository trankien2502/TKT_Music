package com.example.music.fragment;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.music.MyApplication;
import com.example.music.R;
import com.example.music.activity.MainActivity;
import com.example.music.adapter.ArtistGridAdapter;
import com.example.music.constant.GlobalFuntion;
import com.example.music.databinding.FragmentArtistBinding;
import com.example.music.listener.IOnClickArtistItemListener;
import com.example.music.model.Artist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment {
    FragmentArtistBinding mFragmentArtistBinding;
    ArtistGridAdapter mArtistGridAdapter;
    List<Artist> mListArtists;
    static String selectedArtist;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentArtistBinding = FragmentArtistBinding.inflate(inflater, container,false);
        initListener();
        getListArtistSearchFromFirebase("");
        return mFragmentArtistBinding.getRoot();
    }
    private void getListArtistSearchFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getArtistsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListArtists = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Artist artist = dataSnapshot.getValue(Artist.class);
                    if (artist == null) {
                        return;
                    }
                    if (GlobalFuntion.getTextSearch(artist.getName()).toLowerCase().trim()
                            .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())) {
                        mListArtists.add(artist);
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
    private void initListener() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentArtistBinding.rcvArtist.setLayoutManager(gridLayoutManager);
        if (getActivity() == null) {
            return;
        }
        mFragmentArtistBinding.edtSearchArtistName.addTextChangedListener(new TextWatcher() {
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
                getListArtistSearchFromFirebase(strKey);
            }
        });

    }

    private void initAdapter() {
        if (mListArtists!=null){
            mFragmentArtistBinding.noResultArtist.setVisibility(View.GONE);
        }
        if (mListArtists.size() == 0) {
            mFragmentArtistBinding.noResultArtist.setVisibility(View.VISIBLE);
        }
        mArtistGridAdapter = new ArtistGridAdapter(mListArtists, this::goToArtistListSong);
        mFragmentArtistBinding.rcvArtist.setAdapter(mArtistGridAdapter);
    }

    private void goToArtistListSong(Artist artist) {
        selectedArtist = artist.getName();
        replaceFragment(new ArtistListSongFragment());
        MainActivity.mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.artist_list).setChecked(true);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

}
