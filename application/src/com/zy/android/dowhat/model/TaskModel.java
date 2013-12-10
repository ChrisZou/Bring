package com.zy.android.dowhat.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Note;

import com.chriszou.androidorm.AppDBHelper;
import com.chriszou.androidorm.TableOperator;
import com.zy.android.dowhat.beans.Tag;
import com.zy.android.dowhat.beans.Task;

public class TaskModel {
	private volatile static TaskModel singleInstance;
	private Context mContext;
	static {
		List<Class> tables = new ArrayList<Class>();
		tables.add(Note.class);
		tables.add(Tag.class);
		AppDBHelper.setTables(tables, "do_what_db");
	}
	private final List<Task> mAllTasks;

	private TaskModel(Context ctx) {
		mAllTasks = new ArrayList<Task>();
		mContext = ctx;
	}

	public static TaskModel getInstance(Context ctx) {
		if (singleInstance == null) {
			synchronized (TaskModel.class) {
				if (singleInstance == null) {
					singleInstance = new TaskModel(ctx);
				}
			}
		}
		return singleInstance;
	}

	public List<Task> getAllTasks() throws IllegalAccessException {
		if (mAllTasks.size() == 0) {
			TableOperator<Task> tableOperator = new TableOperator<Task>(mContext, Task.class);
			mAllTasks.addAll(tableOperator.getAll());
		}
		return mAllTasks;
	}

	public void addTask(Task task) throws IllegalAccessException {
		TableOperator<Task> tableOperator = new TableOperator<Task>(mContext, Task.class);
		tableOperator.addItem(task);
	}
}
