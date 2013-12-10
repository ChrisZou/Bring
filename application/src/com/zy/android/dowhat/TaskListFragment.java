package com.zy.android.dowhat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.chriszou.androidlibs.L;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.zy.android.dowhat.model.ListModel;

public class TaskListFragment extends Fragment implements OnItemLongClickListener, OnItemClickListener {
	private TaskList mList;
	private BringAdapter mAdapter;
	private DragSortListView mListView;
	public TaskListFragment() {
	}

	public void setList(TaskList list) {
		mList = list;
	}

	public static TaskListFragment newInstance(TaskList list, OnTasksChangedListener l) {
		TaskListFragment fragment = new TaskListFragment();
		fragment.setList(list);
		fragment.setOnTasksChangedListener(l);
		return fragment;
	}

	public String getText() {
		return mList.getName();
	}

	public TaskList getList() {
		return mList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragView = inflater.inflate(R.layout.bring_list, container, false);

		mListView = (DragSortListView) fragView.findViewById(R.id.main_listview);
		mListView.setOnItemClickListener(this);
		mAdapter= new BringAdapter(getActivity(), mList);
		mListView.setAdapter(mAdapter);
		DragSortController controller = buildController(mListView);
		mListView.setFloatViewManager(controller);
		mListView.setOnTouchListener(controller);
		mListView.setDropListener(new DropListener() {
			@Override
			public void drop(int from, int to) {
				String item = mList.remove(from);
				mList.add(to, item);
				ListModel.getInstance(getActivity()).saveList(mList);
				mAdapter.notifyDataSetChanged();
			}
		});
		return fragView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(mListView);
	}

	public DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setSortEnabled(true);
		controller.setDragHandleId(R.id.drag_handle);
		controller.setDragInitMode(DragSortController.ON_DRAG);
		return controller;
	}


	private OnTasksChangedListener mTasksChangedListener;
	public void setOnTasksChangedListener(OnTasksChangedListener l) {
		mTasksChangedListener = l;
	}
	
	
	/**
	 * Remove the item at the given position
	 * @param pos
	 */
	private void remove(final int pos) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Remove item: " + mList.get(pos) + " ?");
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mList.remove(pos);
				// Save to Preference
				ListModel.getInstance(getActivity()).saveList(mList);

				mAdapter.notifyDataSetChanged();
				if (mTasksChangedListener != null) {
					mTasksChangedListener.onTasksChanged();
				}
			}
		});
		builder.create().show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CheckBox cbBox = (CheckBox) view.findViewById(R.id.bring_list_item_checkbox);
		cbBox.toggle();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		remove(position);
		return true;
	}

	public void addItem(String item) {
		if (mList.contains(item)) {
			Toast.makeText(this.getActivity(), "Item " + item + " already exists!", Toast.LENGTH_SHORT).show();
			return;
		}

		mList.add(0, item);
		ListModel.getInstance(getActivity()).saveList(mList);
		mAdapter.notifyDataSetChanged();
		if (mTasksChangedListener != null) {
			mTasksChangedListener.onTasksChanged();
		}
	}
	
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.bringlistview_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (!getUserVisibleHint()) {
			return true;
		}

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		switch (item.getItemId()) {
		case R.id.action_edit:
			editItem(position);
			break;
		case R.id.action_move_to:
			moveToList(position);
			break;
		case R.id.action_remove_item:
			removeItem(position);
			break;
		}
		return super.onContextItemSelected(item);
	}

	private static final int REQUEST_EDIT_ITEM = 100;

	private int editIndex;
	private void editItem(int position) {
		editIndex = position;
		Intent intent = new Intent(getActivity(), PromptDialog_.class);
		intent.putExtra(PromptDialog.EXTRA_STRING_TITLE, "Edit item");
		L.l("size: " + mList.size() + " name: " + mList.getName() + " position: " + position);
		intent.putExtra(PromptDialog.EXTRA_STRING_TIP, mList.get(position));
		startActivityForResult(intent, REQUEST_EDIT_ITEM);
	}

	private void moveToList(final int position) {
		L.l("move to ");
		final ListModel listModel = ListModel.getInstance(getActivity());
		String[] lists = new String[listModel.getAllLists().size()];
		for(int i=0; i<lists.length; i++) {
			lists[i] = listModel.getAllLists().get(i).getName();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setItems(lists, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String item = mList.remove(position);
				listModel.saveList(mList);
				listModel.getAllLists().get(which).add(item);
				listModel.saveList(listModel.getAllLists().get(which));
				notifyChanged();
				mAdapter.notifyDataSetChanged();
			}
		});
		builder.create().show();
	}

	private void removeItem(int position) {
		mList.remove(position);
		ListModel.getInstance(getActivity()).saveList(mList);
		mAdapter.notifyDataSetChanged();
	}
	private void notifyChanged() {
		if (mTasksChangedListener != null) {
			mTasksChangedListener.onTasksChanged();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_EDIT_ITEM && resultCode == Activity.RESULT_OK) {
            String itemName = data.getStringExtra(PromptDialog.EXTRA_RESULT);
            if (mList.contains(itemName)) {
                Toast.makeText(this.getActivity(), "Item " + itemName + " already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

			mList.set(editIndex, itemName);
			ListModel.getInstance(getActivity()).saveList(mList);
			mAdapter.notifyDataSetChanged();
        } 
		super.onActivityResult(requestCode, resultCode, data);
	}



	private static class BringAdapter extends BaseAdapter {

		private TaskList mList;
		private Context mContext;

		public BringAdapter(Context context, TaskList list) {
			mList = list;
			mContext = context;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public String getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.bring_list_item, null);
			}
			CheckBox cBox = (CheckBox) convertView.findViewById(R.id.bring_list_item_checkbox);
			cBox.setText(mList.get(position));
			return convertView;
		}

	}
}
