package com.zy.android.dowhat;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.chriszou.androidlibs.L;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.zy.android.dowhat.beans.Tag;
import com.zy.android.dowhat.beans.Task;
import com.zy.android.dowhat.beans.TaskTag;
import com.zy.android.dowhat.custom.TagView;
import com.zy.android.dowhat.model.TagModel;
import com.zy.android.dowhat.model.TagTaskModel;
import com.zy.android.dowhat.model.TaskModel;

@EActivity(R.layout.task_detail_layout)
public class TaskDetailActivity extends Activity implements OnItemClickListener {

	@ViewById(R.id.task_detail_edit)
	EditText mTaskEdit;

	@ViewById(R.id.task_detail_gridview)
	GridView mGridView;

	private Task mTask;
	private List<Tag> mTaskTags;
	private List<Tag> mAllTags;
	@AfterViews
	void loadData() {
		mTask = (Task) getIntent().getSerializableExtra(Const.Extras.EXTRA_SERIAL_TASK);
		mTaskEdit.setText(mTask.getTitle());

		loadTaskTags();
	}

	TagAdapter mAdapter;
	private void loadTaskTags() {
		mAllTags = TagModel.getInstance(this).copyAll();
		mTaskTags = TagTaskModel.getInstance(getApplicationContext()).getTaskTags(mTask);
		mAdapter = new TagAdapter(mAllTags, mTaskTags);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
	}
	
	

	@Override
	protected void onPause() {
		saveTask();
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		L.l("onbackpressed");
		saveTask();
		super.onBackPressed();
	}

	private void saveTask() {
		String text = mTaskEdit.getText().toString().trim();
		if (!text.equals(mTask.getTitle())) {
			mTask.setTitle(text);
			TaskModel.getInstance(this).updateItem(mTask);
			L.l("update");
			TaskModel.getInstance(this).getTaskFromUuid(mTask.getUuid()).setTitle(text);
		}
	}

	private class TagAdapter extends BaseAdapter {
		private List<Tag> mTags;
		private List<Tag> checkTags;

		public TagAdapter(List<Tag> tags, List<Tag> checkTags) {
			mTags = tags;
			this.checkTags = checkTags;
		}

		@Override
		public int getCount() {
			return mTags.size();
		}

		@Override
		public Tag getItem(int position) {
			return mTags.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tag_list_item, null);
			}
			TagView tagView = (TagView) convertView;
			tagView.setChecked(checkTags.contains(getItem(position)));
			tagView.setText(getItem(position).getTitle());

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Tag tag = (Tag)parent.getItemAtPosition(position);
		if (mTaskTags.contains(tag)) {
			TagTaskModel.getInstance(getApplicationContext()).removeItem(mTask.getUuid(), tag.getUuid());
			mTaskTags.remove(tag);
		} else {
			TaskTag taskTag = new TaskTag(mTask, tag);
			TagTaskModel.getInstance(getApplicationContext()).addItem(taskTag);
			mTaskTags.add(tag);
		}

		mAdapter.notifyDataSetChanged();
	}
}
