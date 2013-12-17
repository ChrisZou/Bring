package com.zy.android.dowhat.model;

import android.content.Context;

import com.zy.android.dowhat.beans.Task;

public class TaskModel extends Model<Task> {
	private volatile static TaskModel singleInstance;

	private TaskModel(Context ctx) {
		super(ctx, Task.class);
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
}
