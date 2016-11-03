package fr.frogdevelopment.pregnancycalendar;

import android.support.annotation.NonNull;

public class Item implements Comparable<Item> {

    final int type;

    final String label;
    final String subLabel;
    final int subBackground;
    final boolean currentWeek;

    Item(String label) {
        this.type = 1;
        this.label = label;
        this.subLabel = null;
        this.subBackground = 0;
        this.currentWeek = false;
    }

    Item(String label, int subBackgroundResource) {
        this.type = 0;
        this.label = label;
        this.subLabel = null;
        this.subBackground = subBackgroundResource;
        this.currentWeek = false;
    }

    Item(String label, String subLabel, int subBackgroundResource, boolean currentWeek) {
        this.type = 2;
        this.label = label;
        this.subLabel = subLabel;
        this.subBackground = subBackgroundResource;
        this.currentWeek = currentWeek;
    }

    @Override
    public int compareTo(@NonNull Item item) {
        return label.compareTo(item.label);
    }
}
