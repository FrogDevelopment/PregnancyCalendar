package fr.frogdevelopment.pregnancycalendar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import fr.frogdevelopment.pregnancycalendar.contraction.ContractionFragment;
import fr.frogdevelopment.pregnancycalendar.infos.InformationFragment;
import fr.frogdevelopment.pregnancycalendar.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                getMenuInflater().inflate(R.menu.menu_main, menu);
                break;

            case 1:
                getMenuInflater().inflate(R.menu.menu_contraction, menu);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment item = null;
            switch (position) {
                case 0:
                    item = new InformationFragment();
                    break;
                case 1:
                    item = new ContractionFragment();
                    break;
            }

            return item;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab_1);
                case 1:
                    return getResources().getString(R.string.tab_2);
            }
            return null;
        }
    }
}
