package com.zy.android.dowhat;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.zy.android.dowhat.adapters.MyAdapter;
import com.zy.android.dowhat.beans.Tag;
import com.zy.android.dowhat.model.TagModel;

public class TagsPopup extends PopupWindow {
	private List<Tag> mSelectedTags;
	
	/**
	 * @param context
	 * @param width
	 * @param height
	 * @param results the list that will hold the selection results
	 */
	public TagsPopup(Context context, int width, int height, List<Tag> results) {
		super(context);
		setWidth(width);
        setHeight(height);
        
        mSelectedTags = results;
		initViews(context);
	}
	
	private void initViews(Context context) {
		ListView listView = new ListView(context);
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		listView.setAdapter(new TagsPopupAdapter(context, TagModel.getInstance(context).copyAll()));
		
		setContentView(listView);
	}
	
	public List<Tag> getSelectedTags() {
		return mSelectedTags;
	}

	private class TagsPopupAdapter extends MyAdapter<Tag> {

		public TagsPopupAdapter(Context context, List<Tag> data) {
			super(context, data);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView==null) {
				CheckBox checkBox = new CheckBox(getContext());
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked) {
							mSelectedTags.add(getItem(position));
						} else {
							mSelectedTags.remove(getItem(position));
						}
					}
				});
				checkBox.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				checkBox.setText(getItem(position).getTitle());
				convertView = checkBox;
			}
			
			return convertView;
		}
	}
}
