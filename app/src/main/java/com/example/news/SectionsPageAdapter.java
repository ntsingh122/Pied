package com.example.news;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPageAdapter extends FragmentPagerAdapter {


    public SectionsPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                OnlineFragment onlineFragment = new OnlineFragment();
                return onlineFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                SavedGroupsFragment savedGroupsFragment = new SavedGroupsFragment();
                return savedGroupsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Online";
            case 1:
                return "Chat";
            case 2:
                return "Saved";
            default: return null;
        }
    }
}