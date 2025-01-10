package com.project.journeyflow.items;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.project.journeyflow.fragments.public_fragments.PublicListFragment;
import com.project.journeyflow.fragments.public_fragments.SharedWithMeFragment;

public class TabPagerAdapterPublic extends FragmentStateAdapter {

    public TabPagerAdapterPublic(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PublicListFragment();
            case 1:
                return new SharedWithMeFragment();
            default:
                return new PublicListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
