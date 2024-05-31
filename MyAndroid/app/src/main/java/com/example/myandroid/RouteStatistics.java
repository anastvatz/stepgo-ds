package com.example.myandroid;

import static android.os.SystemClock.sleep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RouteStatistics extends Fragment {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    DemoCollectionAdapter demoCollectionAdapter;
    ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.route_statistics, container, false);

        TextView textViewFile = rootView.findViewById(R.id.textViewFile);
        TextView textViewDistance = rootView.findViewById(R.id.textViewDistance);
        TextView textViewTime = rootView.findViewById(R.id.textViewTime);
        TextView textViewElevation = rootView.findViewById(R.id.textViewElevation);
        TextView textViewSpeed = rootView.findViewById(R.id.textViewSpeed);

        // Update the text of the TextViews with your calculation results
        textViewFile.setText(String.format("File: %s ",User.gpx_file));
        sleep(1000);
        if (User.total_statistics!=null) {
            textViewDistance.setText(String.format("Distance: %.2f km ", User.total_statistics.get(0)/1000));
            textViewTime.setText(String.format("Time: %.2f min ", User.total_statistics.get(1)/60));
            textViewElevation.setText(String.format("Elevation: %.2f m ", User.total_statistics.get(2)));
            textViewSpeed.setText(String.format("Speed: %.2f m/s", User.total_statistics.get(3)));
        }
        else {
            Toast.makeText(getActivity(),"Error: Master is unreachable or file not found, please try again",Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

}