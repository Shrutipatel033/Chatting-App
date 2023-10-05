package com.example.thirdapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class ChatFragmentAdapter extends FragmentStateAdapter {
    public ChatFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0)
            return new ChatFragment();
        else if(position==1)
            return new StatusFragment();
        else
            return new ProfileFragment();

    }
    @Override
    public int getItemCount() {
        return 3;
    }
}
