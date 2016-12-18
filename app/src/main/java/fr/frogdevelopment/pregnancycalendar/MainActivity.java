package fr.frogdevelopment.pregnancycalendar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.threetenabp.AndroidThreeTen;

import fr.frogdevelopment.pregnancycalendar.contraction.ContractionFragment;
import fr.frogdevelopment.pregnancycalendar.months.MonthsFragment;

public class MainActivity extends AppCompatActivity implements SummaryFragment.RefreshListener {

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

    @Override
    public void onRefresh() {
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment item = null;
            switch (position) {
                case 0:
                    item = new SummaryFragment();
                    break;
                case 1:
                    item = new MonthsFragment();
                    break;
                case 2:
                    item = new ContractionFragment();
                    break;
            }

            return item;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab_1);
                case 1:
                    return getResources().getString(R.string.tab_2);
                case 2:
                    return getResources().getString(R.string.tab_3);
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof SummaryFragment) {
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }
    }
}
