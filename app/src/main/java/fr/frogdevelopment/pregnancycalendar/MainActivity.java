package fr.frogdevelopment.pregnancycalendar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.threetenabp.AndroidThreeTen;

import fr.frogdevelopment.pregnancycalendar.contraction.ContractionFragment;

public class MainActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidThreeTen.init(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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
