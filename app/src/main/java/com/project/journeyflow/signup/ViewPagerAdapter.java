package com.project.journeyflow.signup;

import static com.project.journeyflow.R.color.dark_green;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.project.journeyflow.R;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    int[] sliderAllImages = {
            R.drawable.journey_flow_picture,
            R.drawable.paths_picture,
            R.drawable.share_picture,
    };
    int[] sliderAllTitle = {
            R.string.screen1,
            R.string.screen2,
            R.string.screen3,
    };
    int[] sliderAllDesc = {
            R.string.screen1desc,
            R.string.screen2desc,
            R.string.screen3desc,
    };

    public ViewPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderAllTitle.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_screen,container,false);

        // Font
        Typeface typeface = ResourcesCompat.getFont(context, R.font.aovelsansrounded_rddl);

        // Title and desc
        ImageView sliderImage = view.findViewById(R.id.sliderImage);
        TextView sliderTitle = view.findViewById(R.id.sliderTitle);
        TextView sliderDesc = view.findViewById(R.id.sliderDesc);
        sliderImage.setImageResource(sliderAllImages[position]);

        sliderTitle.setText(this.sliderAllTitle[position]);
        sliderTitle.setTextColor(dark_green);
        sliderTitle.setTypeface(typeface);

        sliderDesc.setText(this.sliderAllDesc[position]);
        sliderDesc.setTextColor(dark_green);
        sliderDesc.setTypeface(typeface);
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
