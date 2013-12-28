package com.zy.android.dowhat.model;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public List<Task> copyAll() {
		List<Task> results = new ArrayList<Task>();
		for (Task task : getAll()) {
			results.add(0, task);
		}

		return results;
	}

	public Task getTaskFromUuid(String key) {
		for (Task task : copyAll()) {
			if (task.getUuid().equals(key)) {
				return task;
			}
		}
		return null;
	}
}
