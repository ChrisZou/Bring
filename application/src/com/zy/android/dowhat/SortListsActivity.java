package com.zy.android.dowhat;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DragSortListener;
import com.zy.android.dowhat.model.ListModel;

@EActivity
public class SortListsActivity extends Activity {

	@ViewById(R.id.sort_listview)
	DragSortListView mListView;

	List<BringList> mLists;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort_lists_layout);
	}

	ArrayAdapter<BringList> mAdapter;
	@AfterViews
	void loadLists() {
		mListView.setDragSortListener(new DragSortListener() {
			@Override
			public void remove(int which) {
			}
			@Override
			public void drag(int from, int to) {
			}
			@Override
			public void drop(int from, int to) {
				BringList list = mLists.remove(from);
				mLists.add(to, list);
				ListModel.getInstance(SortListsActivity.this).saveLists();
				mAdapter.notifyDataSetChanged();
				setResult(RESULT_OK);
			}
		});
		
		mLists = ListModel.getInstance(this).getAllLists();
		mAdapter = new ArrayAdapter<BringList>(this, android.R.layout.simple_expandable_list_item_1, mLists);
		mListView.setAdapter(mAdapter);
	}

}
