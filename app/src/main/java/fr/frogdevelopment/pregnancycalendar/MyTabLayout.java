package fr.frogdevelopment.pregnancycalendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MyTabLayout extends TabLayout {

	public MyTabLayout(Context context) {
		super(context);
	}

	public MyTabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
		super.addTab(tab, position, setSelected);
		LinearLayout tabStrip = ((LinearLayout) getChildAt(0));
		tabStrip.getChildAt(tab.getPosition()).setOnTouchListener((v, event) -> !MyTabLayout.this.isEnabled());
	}
}
