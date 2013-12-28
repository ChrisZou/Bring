package com.zy.android.dowhat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.chriszou.androidlibs.L;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.zy.android.dowhat.beans.Tag;
import com.zy.android.dowhat.beans.Task;
import com.zy.android.dowhat.beans.TaskTag;
import com.zy.android.dowhat.custom.ConfirmDialog;
import com.zy.android.dowhat.custom.ConfirmDialog.OkAction;
import com.zy.android.dowhat.model.TagModel;
import com.zy.android.dowhat.model.TagTaskModel;
import com.zy.android.dowhat.model.TaskModel;

@EActivity(R.layout.tasks_activity)
public class TasksActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
	@ViewById(R.id.main_tags)
	ListView mTagsListView;
	
	@ViewById(R.id.main_tasks)
	ListView mTasksListView;

	private ArrayAdapter<Tag> mTagsAdapter;
	private ArrayAdapter<Task> mTasksAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void loadTestData() {
		TagModel.getInstance(this).removeAll();
		TaskModel.getInstance(getApplicationContext()).removeAll();

		List<Tag> tags = new ArrayList<Tag>();
		List<Task> tasks = new ArrayList<Task>();
		List<TaskTag> taskTags = new ArrayList<TaskTag>();
		for (int i = 0; i < 5; i++) {
			Tag tag = new Tag("tag" + i, UUID.randomUUID().toString(), 0);
			tags.add(tag);
			Task task = new Task("Task " + i);
			tasks.add(task);
			taskTags.add(new TaskTag(task.getUuid(), tag.getUuid()));
		}

		TagModel.getInstance(this).addAll(tags);
		TaskModel.getInstance(this).addAll(tasks);
		TagTaskModel.getInstance(this).addAll(taskTags);

	}

	@ViewById(R.id.main_add_task)
	EditText mAddTask;

	private void initViews() {
		mTagsListView.setOnItemClickListener(this);
		mTasksListView.setOnItemClickListener(this);
		mTagsListView.setOnItemLongClickListener(this);
		mTasksListView.setOnItemLongClickListener(this);

		mAddTask.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					addTask();
					return true;
				}
				return false;
			}
		});
	}

	private void addTask() {
		String title = mAddTask.getText().toString().trim();
		if (title.length() > 0) {
			Task task = new Task(title);
			TaskModel.getInstance(getApplicationContext()).addItem(task);
			mTasksAdapter.clear();
			mTasksAdapter.addAll(TaskModel.getInstance(getApplicationContext()).copyAll());
		}
		mAddTask.setText("");
	}
	
	@AfterViews
	void loadData() {
		initViews();

		mTagsAdapter = new ArrayAdapter<Tag>(this, R.layout.simple_listitem, TagModel.getInstance(this).copyAll());
		mTagsListView.setAdapter(mTagsAdapter);

		mTasksAdapter = new ArrayAdapter<Task>(this, R.layout.simple_listitem, TaskModel.getInstance(this).copyAll());
		mTasksListView.setAdapter(mTasksAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == mTagsListView) {
			loadTagTasks((Tag) parent.getItemAtPosition(position));
		} else if (parent == mTasksListView) {
			viewTask((Task) parent.getItemAtPosition(position));
		}
	}

	@Click(R.id.main_all_tasks)
	void showAllTasks() {
		mTasksAdapter.clear();
		mTasksAdapter.addAll(TaskModel.getInstance(getApplicationContext()).copyAll());
		mTasksAdapter.notifyDataSetChanged();
	}

	@Click(R.id.main_no_tag_tasks)
	void showNoTag() {
		List<Task> noTagTask = new ArrayList<Task>();
		for (Task task : TaskModel.getInstance(getApplicationContext()).copyAll()) {
			if (!TagTaskModel.getInstance(getApplicationContext()).hasTag(task)) {
				noTagTask.add(task);
			}
		}

		mTasksAdapter.clear();
		mTasksAdapter.addAll(noTagTask);
	}

	@Click(R.id.main_add_tag)
	void addTag() {
		Intent intent = new Intent(this, PromptDialog_.class);
		intent.putExtra(PromptDialog.EXTRA_STRING_TITLE, "Add tag");
		startActivityForResult(intent, REQUEST_ADD_TAG);
	}

	private void viewTask(Task task) {
		Intent intent = new Intent(this, TaskDetailActivity_.class);
		intent.putExtra(Const.Extras.EXTRA_SERIAL_TASK, task);
		startActivity(intent);
	}

	private void loadTagTasks(Tag tag) {
		List<Task> tasks = TagTaskModel.getInstance(this).getTasksFromTag(tag);
		L.l("tag: " + tag + ", task size: " + tasks.size());
		mTasksAdapter.clear();
		mTasksAdapter.addAll(tasks);
		mTasksAdapter.notifyDataSetChanged();
	}

	private static final int REQUEST_ADD_TAG = 1;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_ADD_TAG&&resultCode==RESULT_OK) {
			String tagTitle = data.getStringExtra(PromptDialog.EXTRA_RESULT);
			Tag tag = new Tag(tagTitle);
			TagModel.getInstance(getApplicationContext()).addItem(tag);
			mTagsAdapter.add(tag);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == mTagsListView) {
			final Tag tag = (Tag) parent.getItemAtPosition(position);
			ConfirmDialog dialog = new ConfirmDialog(this, "Sure to delete tag: "+tag.getTitle(), new OkAction() {
				@Override
				public void act() {
					TagModel.getInstance(getApplicationContext()).remove(tag);
					TagTaskModel.getInstance(getApplicationContext()).removeTag(tag);
					mTagsAdapter.remove(tag);
				}
			});
			dialog.show();
		} else if (parent == mTasksListView) {
			final Task task = (Task) parent.getItemAtPosition(position);
			ConfirmDialog dialog = new ConfirmDialog(this, "Sure to delete task: " + task.getTitle(), new OkAction() {
				@Override
				public void act() {
					TaskModel.getInstance(getApplicationContext()).remove(task);
					TagTaskModel.getInstance(getApplicationContext()).removeTask(task);
					mTasksAdapter.remove(task);
				}
			});
			dialog.show();
		}
		return false;
	}

}
