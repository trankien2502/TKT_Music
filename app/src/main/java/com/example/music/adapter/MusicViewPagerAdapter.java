package com.example.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.music.fragment.CommentFragment;
import com.example.music.fragment.ListSongPlayingFragment;
import com.example.music.fragment.LyricSongFragment;
import com.example.music.fragment.PlaySongFragment;

public class MusicViewPagerAdapter extends FragmentStateAdapter {

    public MusicViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ListSongPlayingFragment();
        } else if (position==1) {
            return new PlaySongFragment();
        } else if (position==2) {
            return new LyricSongFragment();
        }
        return new CommentFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
