package com.project.journeyflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.journeyflow.R;
import com.project.journeyflow.items.TabPagerAdapterPublic;

public class PublicFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public, container, false);

        showTabPager(view);

        return view;
    }

    private void showTabPager(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutPublic);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerPublic);

        // Set up the PagerAdapter
        TabPagerAdapterPublic adapter = new TabPagerAdapterPublic(requireActivity());
        viewPager.setAdapter(adapter);

        // Link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Public");
                            break;
                        case 1:
                            tab.setText("Shared with me");
                            break;
                    }
                }).attach();
    }
}
