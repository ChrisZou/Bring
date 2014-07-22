package com.zy.android.dowhat.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyAdapter<E> extends BaseAdapter {
	protected List<E> mData;
	private Context mContext;
	protected LayoutInflater mInflater;

	public MyAdapter(Context context, List<E> data) {
		mContext = context;
		mData = data;
		mInflater = LayoutInflater.from(context);
	}
	
	public Context getContext() {
		return mContext;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public E getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
