package com.example.myandroid;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TotalStatistics extends Fragment {

    DemoCollectionAdapter demoCollectionAdapter;
    ViewPager2 viewPager;
    private LinearLayout distanceChartContainer;
    private LinearLayout elevationChartContainer;
    private LinearLayout timeChartContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.total_statistics, container, false);

        int users = 0;
        double distance = 0, time = 0, elevation = 0, speed;
        double avgDistance, avgElevation, avgTime, avgSpeed;
        double total_distance = 0, total_time = 0,total_elevation = 0;
        for (String key : User.user_statistics.keySet()) {
            System.out.println("ENTERED USER STATISTICS");
            users++;
            for (int i = 0; i < User.user_statistics.get(key).size(); i++) {
                total_distance += User.user_statistics.get(key).get(i).get(0); // for every key of the hashmap user_statistics we take the distance and we sum it up
                total_time += User.user_statistics.get(key).get(i).get(1);
                total_elevation += User.user_statistics.get(key).get(i).get(2);
            }
        }
        avgDistance = total_distance/users;
        avgTime = total_time/users;
        avgElevation = total_elevation/users;

        for (int i = 0; i < User.user_statistics.get(User.name).size(); i++) {
            distance += User.user_statistics.get(User.name).get(i).get(0);
            time += User.user_statistics.get(User.name).get(i).get(1);
            elevation += User.user_statistics.get(User.name).get(i).get(2);
        }

        speed = distance/time;

        //ADDED CODE
        distanceChartContainer = rootView.findViewById(R.id.distanceChartContainer);
        elevationChartContainer = rootView.findViewById(R.id.elevationChartContainer);
        timeChartContainer = rootView.findViewById(R.id.timeChartContainer);
        distanceChartContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        elevationChartContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        timeChartContainer.setGravity(Gravity.CENTER_HORIZONTAL);

        addBarChartBar(distanceChartContainer,"User", distance/1000);
        addBarChartBar(timeChartContainer,"User", time/60);
        addBarChartBar(elevationChartContainer,"User", elevation);

        addBarChartBar(distanceChartContainer,"Average", avgDistance/1000);
        addBarChartBar(timeChartContainer,"Average", avgTime/60);
        addBarChartBar(elevationChartContainer,"Average", avgElevation);

        return rootView;
    }

    private void addBarChartBar(LinearLayout chartContainer, String label, double value) {
        // Create a TextView for the bar label
        TextView labelTextView = new TextView(getActivity());
        labelTextView.setText(label);
        labelTextView.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams labelLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelLayoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.bar_chart_bar_margin_bottom));
        labelTextView.setLayoutParams(labelLayoutParams);

        // Create a TextView for the bar value
        TextView valueTextView = new TextView(getActivity());
        String formattedValue = String.format("%.2f", value);  // Format value with 2 decimal places
        valueTextView.setText(formattedValue);
        valueTextView.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams valueLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        valueLayoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.bar_chart_bar_margin_bottom));
        valueTextView.setLayoutParams(valueLayoutParams);

        // Create a View for the bar
        View barView = new View(getActivity());
        barView.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));

        // Add spacing between bars
        LinearLayout.LayoutParams barLayoutParams = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen.bar_chart_bar_width),
                (int) (value * getResources().getDimension(R.dimen.bar_chart_bar_height_multiplier)));
        barLayoutParams.setMargins((int) getResources().getDimension(R.dimen.bar_chart_bar_spacing), 0, 0, 0);
        barView.setLayoutParams(barLayoutParams);

        // Create a LinearLayout to hold the label, value, and the bar
        LinearLayout barContainer = new LinearLayout(getActivity());
        barContainer.setOrientation(LinearLayout.VERTICAL);
        barContainer.addView(barView);
        barContainer.addView(valueTextView);
        barContainer.addView(labelTextView);

        // Add the bar container to the chart container
        chartContainer.addView(barContainer);
    }
}