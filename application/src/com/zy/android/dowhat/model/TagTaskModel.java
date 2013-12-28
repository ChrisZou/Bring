package com.zy.android.dowhat.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.zy.android.dowhat.beans.Tag;
import com.zy.android.dowhat.beans.Task;
import com.zy.android.dowhat.beans.TaskTag;

public class TagTaskModel extends Model<TaskTag> {
	private volatile static TagTaskModel singleInstance;

	private TagTaskModel(Context ctx) {
		super(ctx, TaskTag.class);
	}

	public static TagTaskModel getInstance(Context ctx) {
		if (singleInstance == null) {
			synchronized (TagTaskModel.class) {
				if (singleInstance == null) {
					singleInstance = new TagTaskModel(ctx);
				}
			}
		}
		return singleInstance;
	}

	/**
	 * Get all the tasks with the given tag
	 * 
	 * @param tag
	 * @return
	 */
	public List<Task> getTasksFromTag(Tag tag) {
		List<TaskTag> taskTags = copyAll();
		List<Task> results = new ArrayList<Task>();
		TaskModel taskModel = TaskModel.getInstance(getContext());
		String key = tag.getUuid();
		for (TaskTag taskTag : taskTags) {
			if(key.equals(taskTag.getTagUuid())) {
				Task task = taskModel.getTaskFromUuid(taskTag.getTaskUuid());
				if (task != null) {
					results.add(task);
				}
			}
		}

		return results;
	}

	public List<Tag> getTaskTags(Task mTask) {
		String taskUuid = mTask.getUuid();
		TagModel tagModel = TagModel.getInstance(getContext());
		List<Tag> results = new ArrayList<Tag>();
		for (TaskTag taskTag : copyAll()) {
			if (taskTag.getTaskUuid().equals(taskUuid)) {
				Tag tag = tagModel.getTagFromUuid(taskTag.getTagUuid());
				if (tag != null) {
					results.add(tag);
				}
			}
		}

		return results;
	}

	public void removeItem(String taskUuid, String taguuid) {
		Iterator<TaskTag> iterable = getAll().iterator();
		while(iterable.hasNext()) {
			TaskTag taskTag = iterable.next();
			if (taskTag.getTaskUuid().equals(taskUuid) && taskTag.getTagUuid().equals(taguuid)) {
				iterable.remove();
				try {
					mOperator.delete(taskTag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean hasTag(Task task) {
		for (TaskTag taskTag : getAll()) {
			if (task.getUuid().equals(taskTag.getTaskUuid())) {
				return true;
			}
		}
		return false;
	}

	public void removeTag(Tag tag) {
		Iterator<TaskTag> iterator = getAll().iterator();
		while (iterator.hasNext()) {
			TaskTag taskTag = iterator.next();
			if (taskTag.getTagUuid().equals(tag.getUuid())) {
				iterator.remove();
				try {
					mOperator.delete(taskTag);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void removeTask(Task task) {
		Iterator<TaskTag> iterator = getAll().iterator();
		while (iterator.hasNext()) {
			TaskTag taskTag = iterator.next();
			if (taskTag.getTaskUuid().equals(task.getUuid())) {
				iterator.remove();
				try {
					mOperator.delete(taskTag);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
