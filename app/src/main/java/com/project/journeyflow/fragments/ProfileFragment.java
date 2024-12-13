package com.project.journeyflow.fragments;

import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.journeyflow.R;
import com.project.journeyflow.profile.ProfileTabAdapter;
import com.project.journeyflow.query.ProfileFragmentQuery;
import com.project.journeyflow.query.display.HomeFragmentDisplayData;

public class ProfileFragment extends Fragment {

    private TextView textViewName;
    private ImageView imageViewProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutProfile);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerProfile);

        // Set up ViewPager2 with an adapter
        ProfileTabAdapter adapter = new ProfileTabAdapter(this);
        viewPager.setAdapter(adapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Statistics");
                    break;
                case 1:
                    tab.setText("Calendar");
                    break;
                case 2:
                    tab.setText("Settings");
                    break;
            }
        }).attach();

        initializeViews(view);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        ProfileFragmentQuery query = new ProfileFragmentQuery(requireActivity());
        query.displayNameAndPicture(imageViewProfile, textViewName);
    }

    private void initializeViews(View view) {
        textViewName = view.findViewById(R.id.textViewProfileName);
        imageViewProfile = view.findViewById(R.id.imageViewProfilePicture);
    }
}
