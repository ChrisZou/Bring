package com.zy.android.dowhat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chriszou.androidlibs.L;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.zy.android.dowhat.PromptDialog2.OnOkListener;
import com.zy.android.dowhat.adapters.MyAdapter;
import com.zy.android.dowhat.beans.Tag;
import com.zy.android.dowhat.beans.Task;
import com.zy.android.dowhat.beans.TaskTag;
import com.zy.android.dowhat.custom.ConfirmDialog;
import com.zy.android.dowhat.custom.ConfirmDialog.OkAction;
import com.zy.android.dowhat.model.TagModel;
import com.zy.android.dowhat.model.TagTaskModel;
import com.zy.android.dowhat.model.TaskModel;
import com.zy.android.dowhat.utils.NotificationBarHelper;

@EActivity(R.layout.tasks_activity)
public class TasksActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
	@ViewById(R.id.main_tags)
	ListView mTagsListView;
	
	@ViewById(R.id.main_tasks)
	ListView mTasksListView;

	private TagsAdapter mTagsAdapter;
	private ArrayAdapter<Task> mTasksAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@ViewById(R.id.main_add_task)
	EditText mAddTaskEdit;

	private void initViews() {
		mTagsListView.setOnItemClickListener(this);
		mTasksListView.setOnItemClickListener(this);
		registerForContextMenu(mTagsListView);
		mTasksListView.setOnItemLongClickListener(this);

		mAddTaskEdit.setOnKeyListener(new OnKeyListener() {
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
		String title = mAddTaskEdit.getText().toString().trim();
		if (title.length() > 0) {
			Task task = new Task(title);
			TaskModel.getInstance(getApplicationContext()).addItem(task);
			for(Tag tag: mTagsOfNewTask) {
				TaskTag taskTag = new TaskTag(task, tag);
				TagTaskModel.getInstance(this).addItem(taskTag);
			}
			
			mTasksAdapter.clear();
			mTasksAdapter.addAll(TaskModel.getInstance(getApplicationContext()).copyAll());
			mTagsAdapter.notifyDataSetChanged();
		}
		
		if(mTagsPopup!=null&&mTagsPopup.isShowing()) {
			mTagsPopup.dismiss();
		}
		
		mAddTaskEdit.setText("");
		mTagsOfNewTask.clear();
	}
	
	@AfterViews
	void loadData() {
		initViews();

		mTagsAdapter = new TagsAdapter(this, TagModel.getInstance(this).copyAll());
		mTagsListView.setAdapter(mTagsAdapter);

		mTasksAdapter = new TaskAdapter(this, R.layout.simple_listitem, TaskModel.getInstance(this).copyAll());
		mTasksListView.setAdapter(mTasksAdapter);

		if (mTasksAdapter.getCount() > 0) {
			NotificationBarHelper.showNotification(this, mTasksAdapter.getItem(0).getTitle());
		}
	}

	@Click(R.id.main_all_tasks)
	void showAllTasks() {
		mTagsAdapter.setChecked(-1);
		setTagSelected(findViewById(R.id.main_all_tasks), true);
		setTagSelected(findViewById(R.id.main_no_tag_tasks), false);
	}

	@Click(R.id.main_no_tag_tasks)
	void showNoTag() {

		mTagsAdapter.setChecked(-1);
		setTagSelected(findViewById(R.id.main_no_tag_tasks), true);
		setTagSelected(findViewById(R.id.main_all_tasks), false);
	}
	
	@ViewById(R.id.main_add_task_with_tag)
	ImageView mAddWithTag;
	
	/**
	 * The tags to be attached to the newly created task 
	 */
	private List<Tag> mTagsOfNewTask = new ArrayList<Tag>();
	private TagsPopup mTagsPopup;
	@Click(R.id.main_add_task_with_tag)
	void showTagPopup() {
		mTagsOfNewTask.clear();
		mTagsPopup = new TagsPopup(this, getResources().getDimensionPixelSize(R.dimen.tags_popup_width), getResources().getDimensionPixelSize(R.dimen.tags_popup_height), mTagsOfNewTask);
		mTagsPopup.setOutsideTouchable(true);
		mTagsPopup.showAsDropDown(mAddWithTag);
	}

	public void renameTag(final Tag tag) {
		PromptDialog2 dialog = new PromptDialog2(this, "Rename tag", tag.getTitle(), new OnOkListener() {
			@Override
			public void onOk(String msg) {
				if (msg.trim().length() > 0) {
					tag.setTitle(msg);
					TagModel.getInstance(getApplicationContext()).updateItem(tag);
					mTagsAdapter.notifyDataSetChanged();
				}
			}
		});
		dialog.show();
	}

	@Click(R.id.main_add_tag)
	void addTag() {
		Intent intent = new Intent(this, PromptDialog_.class);
		intent.putExtra(PromptDialog.EXTRA_STRING_TITLE, "Add tag");
		startActivityForResult(intent, REQUEST_ADD_TAG);
	}

	private static final int REQUEST_VIEW_TASK = 2;
	private void viewTask(Task task) {
		Intent intent = new Intent(this, TaskDetailActivity_.class);
		intent.putExtra(Const.Extras.EXTRA_SERIAL_TASK, task);
		startActivityForResult(intent, REQUEST_VIEW_TASK);
	}

	private void loadTagTasks(Tag tag) {
		List<Task> tasks;
		if (tag.equals(Tag.sAll)) {
			tasks = TaskModel.getInstance(this).copyAll();
		} else if (tag.equals(Tag.sNoTag)) {
			tasks = new ArrayList<Task>();
			for (Task task : TaskModel.getInstance(getApplicationContext()).copyAll()) {
				if (!TagTaskModel.getInstance(getApplicationContext()).hasTag(task)) {
					tasks.add(task);
				}
			}
		} else {
			tasks = TagTaskModel.getInstance(this).getTasksFromTag(tag);
		}
		L.l("tag: " + tag + ", task size: " + tasks.size());
		mTasksAdapter.clear();
		mTasksAdapter.addAll(tasks);
		mTasksAdapter.notifyDataSetChanged();
	}

	private void removeTag(final Tag tag) {
		ConfirmDialog dialog = new ConfirmDialog(this, "Sure to delete tag " + tag.getTitle(), new OkAction() {
			@Override
			public void act() {
				TagModel.getInstance(getApplicationContext()).remove(tag);
				TagTaskModel.getInstance(getApplicationContext()).removeTag(tag);
				mTagsAdapter.remove(tag);
			}
		});
		dialog.show();
	}
	
	private void removeTask(final Task task) {
		ConfirmDialog dialog = new ConfirmDialog(this, "Sure to delete task " + task.getTitle(), new OkAction() {
			@Override
			public void act() {
				TaskModel.getInstance(getApplicationContext()).remove(task);
				TagTaskModel.getInstance(getApplicationContext()).removeTask(task);
				mTasksAdapter.remove(task);
			}
		});
		dialog.show();
	}

	private static void setTagSelected(View view, boolean selected) {
		int paddingLeft = view.getPaddingLeft();
		int paddingRight = view.getPaddingRight();
		int paddingTop = view.getPaddingTop();
		int paddingBottom = view.getPaddingBottom();

		int bgRes = selected ? R.drawable.item_active : R.drawable.shape_transparent;
		view.setBackgroundResource(bgRes);

		view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.main_tag_list, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_rename_tag:
			int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
			renameTag(mTagsAdapter.getItem(position));
			break;
		case R.id.action_remove_tag:
			int position1 = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
			removeTag(mTagsAdapter.getItem(position1));
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == mTagsListView) {
			loadTagTasks((Tag) parent.getItemAtPosition(position));
			mTagsAdapter.setChecked(position);
			setTagSelected(findViewById(R.id.main_all_tasks), false);
			setTagSelected(findViewById(R.id.main_no_tag_tasks), false);
		} else if (parent == mTasksListView) {
			viewTask((Task) parent.getItemAtPosition(position));
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == mTagsListView) {
			Tag tag = (Tag) parent.getItemAtPosition(position);
			removeTag(tag);
		} else if (parent == mTasksListView) {
			Task task = (Task) parent.getItemAtPosition(position);
			removeTask(task);
		}
		return false;
	}

	private static final int REQUEST_ADD_TAG = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ADD_TAG && resultCode == RESULT_OK) {
			String tagTitle = data.getStringExtra(PromptDialog.EXTRA_RESULT);
			Tag tag = new Tag(tagTitle);
			TagModel.getInstance(getApplicationContext()).addItem(tag);
			mTagsAdapter.add(tag);
		} else if (requestCode == REQUEST_VIEW_TASK) {
			L.l("on activity result");
			mTagsAdapter.notifyDataSetChanged();
			mTasksAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class TagsAdapter extends MyAdapter<Tag> {

		public TagsAdapter(Context context, List<Tag> data) {
			super(context, data);
		}

		public void remove(Tag tag) {
			mData.remove(tag);
			notifyDataSetChanged();
		}

		public void add(Tag tag) {
			mData.add(tag);
			notifyDataSetChanged();
		}

		private int mCheck = -1;
		public void setChecked(int position) {
			mCheck = position;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null) {
				convertView = mInflater.inflate(R.layout.simple_listitem, null);
			}
			TextView title = (TextView) convertView;
			Tag tag = getItem(position);
			int count = TagTaskModel.getInstance(getContext()).getTasksFromTag(tag).size();
			title.setText(tag.getTitle() + "(" + count + ")");
			setTagSelected(title, mCheck == position);

			return convertView;
		}

	}
	
	private class TaskAdapter extends ArrayAdapter<Task> {

		public TaskAdapter(Context context, int resource, List<Task> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			Task task = getItem(position);
			
			if (task.getTitle().endsWith("!") || task.getTitle().endsWith("！")) {
				view.setBackgroundColor(Color.YELLOW);
			}
			if(task.getTitle().startsWith("!!")||task.getTitle().startsWith("！！")) {
				view.setBackgroundColor(Color.RED);
			}
			return view;
		}
		
		
	}
}
