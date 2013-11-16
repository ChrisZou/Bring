package com.zy.android.bring;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class TabContentFragment extends Fragment implements OnItemLongClickListener, OnItemClickListener {
	private BringList mList;
	private BringAdapter mAdapter;
	public TabContentFragment() {
	}

	public void setList(BringList list) {
		mList = list;
	}

	public static TabContentFragment newInstance(BringList list) {
		TabContentFragment fragment = new TabContentFragment();
		fragment.setList(list);
		return fragment;
	}

	public String getText() {
		return mList.getName();
	}

	public BringList getList() {
		return mList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragView = inflater.inflate(R.layout.bring_list, container, false);

		ListView listView = (ListView) fragView.findViewById(R.id.main_listview);
		listView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);
		mAdapter= new BringAdapter(getActivity(), mList);
		listView.setAdapter(mAdapter);

		return fragView;
	}

	/**
	 * Remove the item at the given position
	 * @param pos
	 */
	private void remove(final int pos) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Remove item: " + mList.get(pos) + " ?");
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mList.remove(pos);

				// Save to Preference
				String items = TextUtils.join(BringActivity.ITEM_SPLITER, mList);
				PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
						.putString(BringActivity.PREF_STRING_LIST_ + mList.getName(), items).commit();
				mAdapter.notifyDataSetChanged();
			}
		});
		builder.create().show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		remove(position);
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CheckBox cbBox = (CheckBox)view.findViewById(R.id.bring_list_item_checkbox);
		cbBox.toggle();
	}
	
	
	
	
	
	

	private class BringAdapter extends BaseAdapter {

		BringList mList;
		Context mContext;

		public BringAdapter(Context context, BringList list) {
			mList = list;
			mContext = context;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public String getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.bring_list_item, null);
			}
			CheckBox cBox = (CheckBox) convertView.findViewById(R.id.bring_list_item_checkbox);
			cBox.setText(mList.get(position).toString());
			return convertView;
		}

	}
}
