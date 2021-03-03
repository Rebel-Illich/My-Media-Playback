package com.hunter.mymediaplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.hunter.mymediaplayer.PlayerFragment.SEEKBAR_DURATION_ACTION;
import static com.hunter.mymediaplayer.PlayerFragment.SEEKER_POSITION_ACTION;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private PlayerController playerController;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission();

        initViewPager();
    }

    private void permission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);

        } else {
            Toast.makeText(this,"Permission Granted !", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleBroadcastReceiver(BroadcastReceiver broadcastReceiver) {
        if (this.broadcastReceiver != null) {
            unregisterReceiver(this.broadcastReceiver);
        }
        this.broadcastReceiver = broadcastReceiver;
        registerCurrentReciever();
    }

    private void registerCurrentReciever() {
        if (broadcastReceiver != null) {
            registerReceiver(broadcastReceiver, new IntentFilter(SEEKBAR_DURATION_ACTION));
            registerReceiver(broadcastReceiver, new IntentFilter(SEEKER_POSITION_ACTION));
            registerReceiver(broadcastReceiver, new IntentFilter("REQUEST_NEXT"));
            registerReceiver(broadcastReceiver, new IntentFilter("TRACK_FINISHED"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerCurrentReciever();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Permission Granted !", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {
        this.playerController = new PlayerController(this);
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new PlayerFragment(this.playerController), "Player");
        viewPagerAdapter.addFragments(new SongsFragment(this.playerController), "Songs");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public static  class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragmentArrayList;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragmentArrayList = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title){
            fragmentArrayList.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}