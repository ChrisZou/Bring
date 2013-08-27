package com.zy.android.bring;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BringActivity extends Activity {
	
	private List<String> mItems = new ArrayList<String>();
	private LinearLayout mViewList = null;
	private PowerManager.WakeLock mWakeLock;
	
	private static final String ITEM_SPLITER = "::";
	private static final String PREF_STRING_ITEM_NAME = "ITEMS";
	
	private SharedPreferences mPreferences;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        initViews();
        addDefaultItems();
        addViews();
    }
	
	private void initViews() {
	    mViewList = (LinearLayout)findViewById(R.id.itemList);
		//Unset all the checkbox items
        findViewById(R.id.main_reset).setOnClickListener(mClickListener);
        findViewById(R.id.main_add).setOnClickListener(mClickListener);
        findViewById(R.id.main_close).setOnClickListener(mClickListener);
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    
	    //Keep the screen on
		if(mWakeLock==null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
	        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getSimpleName()); 
	        mWakeLock.acquire(); 
		}
	}

	@Override
	protected void onPause() {
	    //Release the screen on lock
		mWakeLock.release();
		mWakeLock = null;
		super.onPause();
	}

	private void addViews() {
		for(int i=0; i<mItems.size(); i++){
			addCheckBox(mItems.get(i));
		}
	}
	
	private void addCheckBox(String title) {
		CheckBox cb = new CheckBox(this);
		cb.setText(title);
		cb.setOnClickListener(mCBLister);
		cb.setOnLongClickListener(mLongClickListener);
		mViewList.addView(cb);
	}
	
	private OnLongClickListener mLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            removeItem((CheckBox)v);
            return true;
        }
    };
    
	private void removeItem(CheckBox checkBox) {
        //Remove the item from shared preference
        String title = checkBox.getText().toString();
        StringBuilder sbItems = new StringBuilder(mPreferences.getString(PREF_STRING_ITEM_NAME, ""));
        int start = sbItems.indexOf(title)-2;
        sbItems.delete(start, start+title.length()+2);
        mPreferences.edit().putString("ITEMS", sbItems.toString()).commit();
        
        mItems.remove(title);
        mViewList.removeView(checkBox);
	}

	private void addDefaultItems() {
        String strItems = mPreferences.getString(PREF_STRING_ITEM_NAME, "");  
        String[] arrItems = strItems.split(ITEM_SPLITER);
        for(String str:arrItems) {
        	if(str!=null&&!str.trim().equals("")) {
        		mItems.add(str);
        	}
        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0&&resultCode==RESULT_OK) {
			String itemName = data.getStringExtra("ITEM_NAME");
			if(mItems.contains(itemName)) {
				Toast.makeText(this, "Item "+itemName+" already exists!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			//Add an item to the page
			mItems.add(itemName);
			addCheckBox(itemName);
			
			//Save to Preference
	        String strItems = mPreferences.getString(PREF_STRING_ITEM_NAME, "");  
	        strItems = strItems+ITEM_SPLITER+itemName;
	        
	        mPreferences.edit().putString(PREF_STRING_ITEM_NAME, strItems).commit();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void addItem() {
	    Intent intent = new Intent(BringActivity.this, AddItemActivity.class);
        BringActivity.this.startActivityForResult(intent, 0);
	}
	
	/**
	 * Enable and uncheck all the items
	 */
	private void resetItems() {
        int childCount = mViewList.getChildCount();
        for(int i=0; i<childCount; i++) {
            CheckBox cb = (CheckBox)mViewList.getChildAt(i);
            cb.setEnabled(true);
            cb.setChecked(false);
        }
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
            case R.id.main_add:
                addItem(); break;
            case R.id.main_close:
                finish(); break;
            case R.id.main_reset:
                resetItems();
                break;
            }
        }
    };
    
    private View.OnClickListener mCBLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
        }
    };
    
	@Override
	protected void onDestroy() {
		if(mWakeLock!=null) {
			mWakeLock.release();
		}
		super.onDestroy();
	}
	
}