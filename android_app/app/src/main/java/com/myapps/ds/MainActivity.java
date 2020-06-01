package com.myapps.ds;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.io.File;

public class MainActivity extends FragmentActivity {

    public static final Consumer consumer = new Consumer();
    public static File cacheDir;

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments = new Fragment[2];
            fragments[0] = new ArtistsFragment();
            fragments[1] = new DownloadsFragment();
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return fragments[0];
                case 1: return fragments[1];
            }
            return new ArtistsFragment();
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem artistsMenu = new AHBottomNavigationItem("Artists", R.drawable.artists_foreground);
        AHBottomNavigationItem downloadsMenu = new AHBottomNavigationItem("DownLoads", R.drawable.download_foreground);

        bottomNavigation.addItem(artistsMenu);
        bottomNavigation.addItem(downloadsMenu);

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);
        viewPager.setCurrentItem(0);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                viewPager.setCurrentItem(position);
                return true;
            }
        });

        cacheDir = getCacheDir();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.consumer.setContext(getApplicationContext());
    }

}
