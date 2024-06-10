package com.example.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.music.fragment.UserInformationFragment;
import com.example.music.fragment.AllSongsFragment;
import com.example.music.fragment.ArtistFragment;
import com.example.music.fragment.ContactFragment;
import com.example.music.fragment.FeaturedSongsFragment;
import com.example.music.fragment.FeedbackFragment;
import com.example.music.fragment.HomeFragment;
import com.example.music.fragment.NewSongsFragment;
import com.example.music.fragment.PlaylistFragment;
import com.example.music.fragment.PopularSongsFragment;
import com.example.music.fragment.SettingFragment;

public class MyAppViewPagerAdapter extends FragmentStateAdapter {


    public MyAppViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new PlaylistFragment();
            case 2:
                return new ArtistFragment();
            case 3:
                return new UserInformationFragment();
            case 4:
                return new SettingFragment();
            case 5:
                return new AllSongsFragment();
            case 6:
                return new FeaturedSongsFragment();
            case 7:
                return new PopularSongsFragment();
            case 8:
                return new NewSongsFragment();
            case 9:
                return new FeedbackFragment();
            case 10:
                return new ContactFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 11;
    }
}
