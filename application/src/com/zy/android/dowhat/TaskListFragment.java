package com.zy.android.dowhat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.zy.android.dowhat.model.ListModel;

public class TaskListFragment extends Fragment implements OnItemLongClickListener, OnItemClickListener {
    public static final int REQUEST_ADD_ITEM = 0;
	private BringList mList;
	private BringAdapter mAdapter;
	public TaskListFragment() {
	}

	public void setList(BringList list) {
		mList = list;
	}

	public static TaskListFragment newInstance(BringList list, OnTasksChangedListener l) {
		TaskListFragment fragment = new TaskListFragment();
		fragment.setList(list);
		fragment.setOnTasksChangedListener(l);
		return fragment;
	}

	public String getText() {
		return mList.getName();
	}

	public BringList getList() {
		return mList;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragView = inflater.inflate(R.layout.bring_list, container, false);

		DragSortListView listView = (DragSortListView) fragView.findViewById(R.id.main_listview);
		listView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);
		mAdapter= new BringAdapter(getActivity(), mList);
		listView.setAdapter(mAdapter);
		DragSortController controller = buildController(listView);
		listView.setFloatViewManager(controller);
		listView.setOnTouchListener(controller);
		listView.setDropListener(new DropListener() {
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_ITEM && resultCode == Activity.RESULT_OK) {
            String itemName = data.getStringExtra(PromptDialog.EXTRA_RESULT);
            if (mList.contains(itemName)) {
                Toast.makeText(this.getActivity(), "Item " + itemName + " already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            mList.add(itemName);
			ListModel.getInstance(getActivity()).saveList(mList);
			mAdapter.notifyDataSetChanged();
        } 
		super.onActivityResult(requestCode, resultCode, data);
	}



	private static class BringAdapter extends BaseAdapter {

		private BringList mList;
		private Context mContext;

		public BringAdapter(Context context, BringList list) {
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
