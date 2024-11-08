package com.project.journeyflow.signup;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.project.journeyflow.MainActivity;
import com.project.journeyflow.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class NavigationActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton, nextButton, skipButton;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onPageSelected(int position) {
            setDotIndicator(position);
            if (position > 0) {
                backButton.setVisibility(View.VISIBLE);
            } else {
                backButton.setVisibility(View.INVISIBLE);
            }
            if (position == 2){
                nextButton.setText("Finish");
            } else {
                nextButton.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Realm.init(this);
        // on below line we are creating a variable for realm configuration and initializing it.
        RealmConfiguration config = new RealmConfiguration.Builder()
                // below line is to allow write
                // data to database on ui thread.
                .allowWritesOnUiThread(true)
                // below line is to delete realm
                // if migration is needed.
                .deleteRealmIfMigrationNeeded()
                // at last we are calling a build method to generate the configurations.
                .build();
        // on below line we are setting the default
        // configuration to our realm database.
        Realm.setDefaultConfiguration(config);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        //skipButton = findViewById(R.id.skipButton);

        slideViewPager = findViewById(R.id.slideViewPager);
        dotIndicator = findViewById(R.id.dotIndicator);
        viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);
        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);

        backButton.setOnClickListener(view -> {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        nextButton.setOnClickListener(view -> {
            if (getItem(0) < 2)
                slideViewPager.setCurrentItem(getItem(1), true);
            else {
                Intent i = new Intent(NavigationActivity.this, GetStartedActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void setDotIndicator(int position) {
        dots = new TextView[3];
        dotIndicator.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.green));
            dotIndicator.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.dark_green));
    }
    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }
}