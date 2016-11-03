package fr.frogdevelopment.pregnancycalendar;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Item> titles;
    private HashMap<Item, List<Item>> weeks;

    public CustomExpandableListAdapter(Context context, HashMap<Item, List<Item>> data) {
        this.context = context;
        this.titles = new ArrayList<>(data.keySet());
        Collections.sort(titles);
        this.weeks = data;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.weeks.get(this.titles.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Item childItem = (Item) getChild(listPosition, expandedListPosition);

        if (childItem.type == 1) {
            return getDateView(childItem, convertView, parent);
        } else {
            return getWeekView(childItem, convertView, parent);
        }
    }

    private View getDateView(Item childItem, View convertView, ViewGroup parent) {
        if (convertView == null || !(convertView instanceof TextView)) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.row_week_date, parent, false);
        }

        TextView textView = (TextView) convertView;
        textView.setText(childItem.label);

        return convertView;
    }

    private View getWeekView(Item childItem, View convertView, ViewGroup parent) {
        if (convertView == null || !(convertView instanceof LinearLayout)) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.row_week_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.week_item);
        textView.setText(childItem.label);

        if (childItem.currentWeek) {
            textView.setBackgroundResource(R.color.colorPrimary);
        } else {
            textView.setBackgroundResource(0);
        }

        TextView subItemView = (TextView) convertView.findViewById(R.id.week_sub_item);
        if (TextUtils.isEmpty(childItem.subLabel)) {
            subItemView.setVisibility(View.GONE);
            subItemView.setText(null);
            subItemView.setBackgroundResource(0);
        } else {
            subItemView.setVisibility(View.VISIBLE);
            subItemView.setText(childItem.subLabel);
            subItemView.setBackgroundResource(childItem.subBackground);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.weeks.get(this.titles.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.titles.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.titles.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Item itemTitle = (Item) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.row_quarter, parent, false);
        }

        TextView listTitleTextView = (TextView) convertView;
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(itemTitle.label);
//        listTitleTextView.setBackgroundResource(itemTitle.subBackground);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
