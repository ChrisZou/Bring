package com.zy.android.bring;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@SuppressLint("ValidFragment")
@EActivity
public class BringActivity extends Activity {

    private List<BringList> mLists = new ArrayList<BringList>();
    private BringList mCurrList;
    
    private PowerManager.WakeLock mWakeLock;

    public static final String ITEM_SPLITER = "::";
    public static final String PREF_STRING_LIST_ = "pref_string_list_";
    public static final String PREF_STRING_LISTS = "pref_string_list_names";

    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @ViewById(R.id.main_add_item)
    TextView mAddItemBtn;
    private static final int REQUEST_ADD_ITEM = 0;
    @Click(R.id.main_add_item)
    void addItem() {
        Intent intent = new Intent(BringActivity.this, PromptDialog.class);
    	intent.putExtra(Const.Extras.EXTRA_STRING_TITLE, "添加项目");
        BringActivity.this.startActivityForResult(intent, REQUEST_ADD_ITEM);
    }
    
    @Click(R.id.main_add_list)
    void addList() {
    	Intent intent = new Intent(BringActivity.this, PromptDialog.class);
    	intent.putExtra(Const.Extras.EXTRA_STRING_TITLE, "添加列表");
        startActivityForResult(intent, REQUEST_ADD_LIST);
    }
    
    @Click(R.id.main_close)
    void exit() {
    	finish();
    }

    @AfterViews
    void loadItems() {
    	mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    	String strLists = mPreferences.getString(PREF_STRING_LISTS, "");
    	String[] lists = strLists.split(ITEM_SPLITER);
    	for(String list:lists){
    		if(list.length()>0) {
    			mLists.add(new BringList(list));
    		}
    	}
    	
    	for(BringList list:mLists) {
    		loadList(list);
    		addTab(list);
    	}
    }

    private void loadList(BringList list){
        String strItems = mPreferences.getString(PREF_STRING_LIST_+list.getName(), "");
        String[] arrItems = strItems.split(ITEM_SPLITER);
        for (String str : arrItems) {
            if (str != null && !(str.trim().length() == 0)) {
                list.add(str);
            }
        }
    }
    
    private void addTab(BringList list) {
        final ActionBar bar = getActionBar();
        final String text = list.getName();
		bar.addTab(bar.newTab().setText(text).setTabListener(new TabListener(TabContentFragment.newInstance(list))));
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        // Keep the screen on
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getSimpleName());
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        // Release the screen lock
        mWakeLock.release();
        mWakeLock = null;
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_ITEM && resultCode == RESULT_OK) {
            String itemName = data.getStringExtra(PromptDialog.EXTRA_RESULT);
            if (mCurrList.contains(itemName)) {
                Toast.makeText(this, "Item " + itemName + " already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add an item to the page
            mCurrList.add(itemName);
            // Save to Preference
            String items = TextUtils.join(ITEM_SPLITER, mCurrList);
            mPreferences.edit().putString(PREF_STRING_LIST_+mCurrList.getName(), items).commit();
        } else if (requestCode == REQUEST_ADD_LIST && resultCode == RESULT_OK) {
            String listName= data.getStringExtra(PromptDialog.EXTRA_RESULT);
            if (mLists.contains(listName)) {
                Toast.makeText(this, "List " + listName + " already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add an item to the page
            BringList newList = new BringList(listName);
            mLists.add(newList);
            addTab(newList);

            // Save to Preference
            String lists = TextUtils.join(ITEM_SPLITER, mLists);
            mPreferences.edit().putString(PREF_STRING_LISTS, lists).commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final int REQUEST_ADD_LIST = 1;

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
    	//Realese Screen light lock
        if (mWakeLock != null) {
            mWakeLock.release();
        }
        super.onDestroy();
    }
    

    private class TabListener implements ActionBar.TabListener {
        private TabContentFragment mFragment;

        public TabListener(TabContentFragment fragment) {
            mFragment = fragment;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            ft.add(R.id.fragment_content, mFragment, mFragment.getText());
            mCurrList = mFragment.getList();
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(mFragment);
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

    }

}