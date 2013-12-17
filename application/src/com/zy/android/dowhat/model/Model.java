package com.zy.android.dowhat.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.chriszou.androidorm.AppDBHelper;
import com.chriszou.androidorm.TableOperator;
import com.zy.android.dowhat.beans.Tag;
import com.zy.android.dowhat.beans.Task;

public abstract class Model<T> {
	static {
		List<Class> tables = new ArrayList<Class>();
		tables.add(Task.class);
		tables.add(Tag.class);
		AppDBHelper.setTables(tables, "do_what_db");
	}
	
	protected Context mContext;

	protected final List<T> mAllItems;

	protected TableOperator<T> mOperator;

	public Model(Context context, Class<T> type) {
		mContext = context;
		mAllItems = new ArrayList<T>();
		mOperator = new TableOperator<T>(context, type);
	}

	public List<T> getAll() {
		if (mAllItems.size() == 0) {
			try {
				mAllItems.addAll(mOperator.getAll());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return mAllItems;
	}

	public void removeAll() {
		mOperator.deleteAll();
		mAllItems.clear();
	}

	public void addItem(T item) {
		try {
			mOperator.delete(item);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
