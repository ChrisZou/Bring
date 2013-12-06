package com.zy.android.dowhat;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.viewpagerindicator.TabPageIndicator;
import com.zy.android.dowhat.model.ListModel;

@EActivity
public class DoWhatActivity extends FragmentActivity implements OnTasksChangedListener {

	public static final String ITEM_SPLITER = "::";
	public static final String PREF_STRING_LIST_ = "pref_string_list_";
	public static final String PREF_STRING_LISTS = "pref_string_list_names";

    private List<BringList> mLists = new ArrayList<BringList>();
	private TasksFragmentAdapter mAdapter;

	@ViewById(R.id.main_pager)
	ViewPager mPager;

	@ViewById(R.id.main_indicator)
	TabPageIndicator mIndicator;

	@ViewById(R.id.main_add_edit)
	EditText mAddEdit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
	private int mNotificationId = 0;
	private void notification() {
		if (mLists.size() > 0 && mLists.get(0).size() > 0) {
			String item = mLists.get(0).get(0);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
			mBuilder.setSmallIcon(R.drawable.todo).setContentTitle("Todo").setContentText(item);
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(this, DoWhatActivity_.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(DoWhatActivity_.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mBuilder.setOngoing(true);
			mNotificationManager.notify(mNotificationId, mBuilder.build());
		}
	}

    void addItem() {
    	String item = mAddEdit.getText().toString().trim();
		if (item.length() > 0) {
			mAdapter.getItem(mPager.getCurrentItem()).addItem(item);
			mAddEdit.setText("");
    	}
    }
    
    void addList() {
		Intent intent = new Intent(DoWhatActivity.this, PromptDialog_.class);
		intent.putExtra(PromptDialog.EXTRA_STRING_TITLE, "添加列表");
        startActivityForResult(intent, REQUEST_ADD_LIST);
    }

	private BringList getCurrentList() {
		return mLists.get(mPager.getCurrentItem());
	}

	void renameList() {
		Intent intent = new Intent(DoWhatActivity.this, PromptDialog_.class);
		intent.putExtra(PromptDialog.EXTRA_STRING_TITLE, "Rename list");
		intent.putExtra(PromptDialog.EXTRA_STRING_TIP, getCurrentList().getName());
		startActivityForResult(intent, REQUEST_RENAME_LIST);
	}

    @AfterViews
    void loadItems() {
    	mLists = ListModel.getInstance(this).getAllLists();
		mAdapter = new TasksFragmentAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
    	mIndicator.setViewPager(mPager);
		notification();
		
		mAddEdit.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
					addItem();
					return true;
				}
				return false;
			}
		});
    }
    

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete: 
			removeCurrentList();
			break;
		case R.id.action_add_list:
			addList();
			break;
		case R.id.action_sort_list:
			sortLists();
			break;
		case R.id.action_rename_list:
			renameList();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}


	private static final int REQUEST_SORT_LISTS = 0;
	private void sortLists() {
		startActivityForResult(new Intent(this, SortListsActivity_.class), REQUEST_SORT_LISTS);
	}

	private void removeCurrentList() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Remove list: " + mLists.get(mPager.getCurrentItem()) + " ?");
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ListModel.getInstance(DoWhatActivity.this).removeList(mLists.get(mPager.getCurrentItem()));

				mPager.setCurrentItem(0);
				mIndicator.notifyDataSetChanged();
				mAdapter.notifyDataSetChanged();
			}
		});
		builder.create().show();
	}


	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RENAME_LIST && resultCode == RESULT_OK) {
            String listName= data.getStringExtra(PromptDialog.EXTRA_RESULT);
			if (ListModel.getInstance(this).hasList(listName)) {
                Toast.makeText(this, "List " + listName + " already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

			ListModel.getInstance(this).renameList(getCurrentList(), listName);
			mIndicator.notifyDataSetChanged();
        }
		if (requestCode == REQUEST_ADD_LIST && resultCode == RESULT_OK) {
			String listName = data.getStringExtra(PromptDialog.EXTRA_RESULT);
			if (ListModel.getInstance(this).hasList(listName)) {
				Toast.makeText(this, "List " + listName + " already exists!", Toast.LENGTH_SHORT).show();
				return;
			}

			ListModel.getInstance(this).addList(listName);
			mAdapter.addList(ListModel.getInstance(getApplicationContext()).getList(listName));
			mPager.setCurrentItem(mAdapter.getCount() - 1);
			mIndicator.notifyDataSetChanged();
		}
		if (requestCode == REQUEST_SORT_LISTS && resultCode == RESULT_OK) {
			mIndicator.notifyDataSetChanged();
			mAdapter.notifyDataSetChanged();
		}
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final int REQUEST_ADD_LIST = 1;
	private static final int REQUEST_RENAME_LIST = 2;

    class TasksFragmentAdapter extends FragmentPagerAdapter {

		List<TaskListFragment> fragments;

		public TasksFragmentAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);

			fragments = new ArrayList<TaskListFragment>();
			for(BringList list:mLists) {
				fragments.add(TaskListFragment.newInstance(list, DoWhatActivity.this));
			}

		}

		public void addList(BringList list) {
			fragments.add(TaskListFragment.newInstance(list, DoWhatActivity.this));
			notifyDataSetChanged();
		}

		public void removeList(int index) {
			fragments.remove(index);
			mIndicator.notifyDataSetChanged();
			notifyDataSetChanged();
		}

		@Override
		public TaskListFragment getItem(int position) {
			return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
			return mLists.get(position).getName() + "(" + mLists.get(position).size() + ")";
        }

        @Override
        public int getCount() {
            return mLists.size();
        }
    }

	@Override
	public void onTasksChanged() {
		mIndicator.notifyDataSetChanged();
	}

}