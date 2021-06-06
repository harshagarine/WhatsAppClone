package com.garine.whatsappclone.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.garine.whatsappclone.R;
import com.garine.whatsappclone.databinding.ActivityMainBinding;
import com.garine.whatsappclone.menu.CallsFragment;
import com.garine.whatsappclone.menu.CameraFragment;
import com.garine.whatsappclone.menu.ChatsFragment;
import com.garine.whatsappclone.menu.StatusFragment;
import com.garine.whatsappclone.view.activities.contact.ContactsActivity;
import com.garine.whatsappclone.view.activities.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        View tab1 = LayoutInflater.from(this).inflate(R.layout.custom_camera_tab, null);
        try {
            binding.tabLayout.getTabAt(0).setCustomView(tab1);
        }catch (Exception e){e.printStackTrace();}

        binding.viewPager.setCurrentItem(1); //

        setSupportActionBar(binding.toolbar);

        binding.fabAction.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContactsActivity.class)));

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabICon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    private void setUpWithViewPager(ViewPager viewPager){
        MainActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment(),"");
        adapter.addFragment(new ChatsFragment(),"Chats");
        adapter.addFragment(new StatusFragment(),"Status");
        adapter.addFragment(new CallsFragment(),"Calls");
        viewPager.setAdapter(adapter);


    }
    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_search :  Toast.makeText(MainActivity.this, "Action Search", Toast.LENGTH_LONG).show(); break;
            case R.id.action_new_group :  Toast.makeText(MainActivity.this, "Action New Group", Toast.LENGTH_LONG).show(); break;
            case R.id.action_new_broadcast :  Toast.makeText(MainActivity.this, "Action New Broadcast", Toast.LENGTH_LONG).show(); break;
            case R.id.action_wa_web :  Toast.makeText(MainActivity.this, "Action Web", Toast.LENGTH_LONG).show(); break;
            case R.id.action_starred_message :  Toast.makeText(MainActivity.this, "Action Starred Message", Toast.LENGTH_LONG).show(); break;
            case R.id.action_settings :
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeFabICon(final int index){
        binding.fabAction.hide();

        new Handler().postDelayed(() -> {
            switch (index){

                case 0 :      binding.fabAction.hide(); break;
                case 1 :
                    binding.fabAction.show();
                    binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_chat_black_24dp));
                            binding.fabAction.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContactsActivity.class)));

                break;
                case 2 :
                    binding.fabAction.show(); binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_camera_alt_black_24dp)); break;
                case 3 :   binding.fabAction.show();  binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_call_black_24dp)); break;
            }

        },400);

    }
}
