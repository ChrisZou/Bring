package com.zy.android.bring;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.viewpagerindicator.TabPageIndicator;
import com.zy.android.bring.model.ListModel;

@EActivity
public class BringActivity extends FragmentActivity {

	public static final String ITEM_SPLITER = "::";
	public static final String PREF_STRING_LIST_ = "pref_string_list_";
	public static final String PREF_STRING_LISTS = "pref_string_list_names";

    private List<BringList> mLists = new ArrayList<BringList>();
    private SharedPreferences mPreferences;
	private BringListAdapter mAdapter;

	@ViewById(R.id.main_pager)
	ViewPager mPager;


	@ViewById(R.id.main_indicator)
	TabPageIndicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    @Click(R.id.main_add_item)
    void addItem() {
		mAdapter.getItem(mPager.getCurrentItem()).addItem();
    }
    
    @Click(R.id.main_add_list)
    void addList() {
		Intent intent = new Intent(BringActivity.this, PromptDialog_.class);
    	intent.putExtra(Const.Extras.EXTRA_STRING_TITLE, "添加列表");
        startActivityForResult(intent, REQUEST_ADD_LIST);
    }
    
    @Click(R.id.main_close)
    void exit() {
    	finish();
    }

    @AfterViews
    void loadItems() {
    	mLists = ListModel.getInstance(this).getAllLists();
		mAdapter = new BringListAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
    	mIndicator.setViewPager(mPager);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ADD_LIST && resultCode == RESULT_OK) {
            String listName= data.getStringExtra(PromptDialog.EXTRA_RESULT);
            if (mLists.contains(listName)) {
                Toast.makeText(this, "List " + listName + " already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

			ListModel.getInstance(this).addList(listName);
			mAdapter.addList(ListModel.getInstance(getApplicationContext()).getList(listName));
			mPager.setCurrentItem(mAdapter.getCount() - 1);
			mIndicator.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final int REQUEST_ADD_LIST = 1;

    class BringListAdapter extends FragmentPagerAdapter {

		List<TabContentFragment> fragments;

		public BringListAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);

			fragments = new ArrayList<TabContentFragment>();
			for(BringList list:mLists) {
				fragments.add(TabContentFragment.newInstance(list));
			}

		}

		public void addList(BringList list) {
			fragments.add(TabContentFragment.newInstance(list));
			notifyDataSetChanged();
		}

		public void removeList(int index) {
			fragments.remove(index);
			notifyDataSetChanged();
		}

		@Override
		public TabContentFragment getItem(int position) {
			return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mLists.get(position).getName();
        }

        @Override
        public int getCount() {
            return mLists.size();
        }
    }

}