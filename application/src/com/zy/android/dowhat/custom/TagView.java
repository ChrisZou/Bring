package com.zy.android.dowhat.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class TagView extends TextView {

	private boolean mChecked = false;
	private ColorDrawable mCheckedDrawable;
	private ColorDrawable mUncheckeDrawable;
	public TagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mUncheckeDrawable = new ColorDrawable(Color.parseColor("#eeffffff"));
		mCheckedDrawable = new ColorDrawable(Color.parseColor("#ee005500"));
	}

	public void setChecked(boolean checked) {
		mChecked = checked;
		onCheckedChanged();
	}

	private void onCheckedChanged() {
		if (mChecked) {
			setBackground(mCheckedDrawable);
		} else {
			setBackground(mUncheckeDrawable);
		}
	}
}
