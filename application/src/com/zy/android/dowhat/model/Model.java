package com.zy.android.dowhat.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.chriszou.androidorm.AppDBHelper;
import com.chriszou.androidorm.TableOperator;
import com.zy.android.dowhat.beans.Tag;
import com.zy.android.dowhat.beans.Task;
import com.zy.android.dowhat.beans.TaskTag;

public abstract class Model<T extends Cloneable> {
	static {
		@SuppressWarnings("rawtypes")
		List<Class> tables = new ArrayList<Class>();
		tables.add(Task.class);
		tables.add(Tag.class);
		tables.add(TaskTag.class);
		AppDBHelper.setTables(tables, "do_what_db");
	}

	protected Context mContext;

	protected final List<T> mAllItems;

	protected TableOperator<T> mOperator;

	public Model(Context context, Class<T> type) {
		mContext = context;
		mOperator = new TableOperator<T>(context, type);
		mAllItems = new ArrayList<T>();
		init();
	}

	private void init() {
		try {
			mAllItems.addAll(mOperator.getAll());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	protected List<T> getAll() {
		return mAllItems;
	}

	public Context getContext() {
		return mContext;
	}

	public List<T> copyAll() {
		List<T> results = new ArrayList<T>();
		for (T item : mAllItems) {
			results.add(item);
		}

		return results;
	}

	public void updateItem(T t) {
		try {
			mOperator.updateItem(t);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void removeAll() {
		mOperator.deleteAll();
		mAllItems.clear();
	}

	public void remove(T item) {
		mAllItems.remove(item);
		try {
			mOperator.delete(item);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void addItem(T item) {
		try {
			mOperator.insertItem(item);
			mAllItems.add(item);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void addItem(int index, T item) {
		try {
			mOperator.insertItem(item);
			mAllItems.add(index, item);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void addAll(List<T> items) {
		try {
			mOperator.insertAll(items);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
