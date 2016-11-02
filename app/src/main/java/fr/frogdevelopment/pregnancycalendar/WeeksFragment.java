package fr.frogdevelopment.pregnancycalendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.frogdevelopment.pregnancycalendar.PregnancyUtils.amenorrheaDate;

public class WeeksFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExpandableListView rootView = (ExpandableListView) inflater.inflate(R.layout.fragment_weeks, container, false);


        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        long currentWeek = PregnancyUtils.getCurrentWeek(PregnancyUtils.amenorrheaDate, LocalDate.now());

        HashMap<Item, List<Item>> items = new HashMap<>();

        List<Item> weeks = new ArrayList<>();

        int currentColor;
        int currentSubColor;
        String subLabel;

        int groupPosition = 0;
        int child = -1;
        int childPosition = 0;

        for (int week = 1; week <= 42; week++) {

            switch (week) {
                case 1:
                    currentColor = R.color.quarter_1;
                    items.put(new Item(getResources().getString(R.string.quarter_1), currentColor), weeks);

                    subLabel = null;
                    currentSubColor = 0;

                    groupPosition = 0;
                    childPosition = 0;
                    break;

                case 11:
                case 12:
                case 13:
                    subLabel = getResources().getString(R.string.sonogram_1);
                    currentSubColor = R.color.sonogram_1;
                    break;

                case 14:
                case 15:
//                    case 16:
                case 17:
                case 18:
                    subLabel = getResources().getString(R.string.trisomy_21);
                    currentSubColor = R.color.trisomy_21;
                    break;

                case 16:
                    subLabel = getResources().getString(R.string.trisomy_21);
                    currentSubColor = R.color.trisomy_21;

                    currentColor = R.color.quarter_2;
                    weeks = new ArrayList<>();
                    items.put(new Item(getResources().getString(R.string.quarter_2), currentColor), weeks);

                    if (child == -1) {
                        groupPosition = 1;
                        childPosition = 0;
                    }
                    break;

                case 22:
                case 23:
                case 24:
                    subLabel = getResources().getString(R.string.sonogram_2);
                    currentSubColor = R.color.sonogram_2;
                    break;

                case 29:
                    currentColor = R.color.quarter_3;
                    weeks = new ArrayList<>();
                    items.put(new Item(getResources().getString(R.string.quarter_3), currentColor), weeks);
                    subLabel = null;
                    currentSubColor = currentColor;

                    if (child == -1) {
                        groupPosition = 2;
                        childPosition = 0;
                    }
                    break;

                case 32:
                case 33:
                case 34:
                    subLabel = getResources().getString(R.string.sonogram_3);
                    currentSubColor = R.color.sonogram_3;
                    break;

                default:
                    subLabel = null;
                    currentSubColor = 0;
                    break;
            }
            if (currentWeek == week) {
                child = childPosition;
            }

            weeks.add(new Item(getResources().getString(R.string.week, week, amenorrheaDate.plusWeeks(week).format(dateTimeFormatter)), subLabel, currentSubColor, currentWeek == week));

            childPosition++;
        }

        ExpandableListAdapter expandableListAdapter = new CustomExpandableListAdapter(getContext(), items);

        rootView.setAdapter(expandableListAdapter);
        if (child >= 0) {
            rootView.expandGroup(groupPosition);
            rootView.setSelectedChild(groupPosition, child, true);
        }

//            rootView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//                @Override
//                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                    return false;
//                }
//            });


        return rootView;
    }

}
