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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BringActivity extends Activity {
	
	private List<String> mItems = new ArrayList<String>();
	private LinearLayout mItemList = null;
	private PowerManager.WakeLock mWakeLock;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initViews();
        addDefaultItems();
        addViews();
    }
	
	private void initViews() {
	    mItemList = (LinearLayout)findViewById(R.id.itemList);
		//Unset all the checkbox items
        findViewById(R.id.reset).setOnClickListener(mClickListener);
        findViewById(R.id.add).setOnClickListener(mClickListener);
        findViewById(R.id.close).setOnClickListener(mClickListener);
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
		mItemList.addView(cb);
	}
	
	private OnLongClickListener mLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            removeItem((CheckBox)v);
            return false;
        }
    };
	private void removeItem(CheckBox checkBox) {
        //Remove the item from shared preference
        String title = checkBox.getText().toString();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this); 
        StringBuilder sbItems = new StringBuilder(sp.getString("ITEMS", ""));
        int start = sbItems.indexOf(title)-2;
        sbItems.delete(start, start+title.length()+2);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ITEMS", sbItems.toString());
        editor.commit();
        
        mItems.remove(title);
        mItemList.removeView(checkBox);
	}

	private void addDefaultItems() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String strItems = sp.getString("ITEMS", "");  
        String[] arrItems = strItems.split("&&");
        for(String str:arrItems) {
        	if(str!=null&&!str.trim().equals("")) {
        		Log.d("zy", "item:"+str.length());
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
	        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	        String strItems = sp.getString("ITEMS", "");  
	        strItems = strItems+"&&"+itemName;
	        sp.edit().putString("ITEMS", strItems).commit();
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
        int childCount = mItemList.getChildCount();
        for(int i=0; i<childCount; i++) {
            CheckBox cb = (CheckBox)mItemList.getChildAt(i);
            cb.setEnabled(true);
            cb.setChecked(false);
        }
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
            case R.id.add:
                addItem(); break;
            case R.id.close:
                finish(); break;
            case R.id.reset:
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