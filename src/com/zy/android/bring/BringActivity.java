package com.zy.android.bring;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BringActivity extends Activity {
	
	private List<String> mItems = new ArrayList<String>();
	private LinearLayout mItemList = null;
	private PowerManager.WakeLock mWakeLock;
	
	private String PREF_NAME = "items_saved";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mItemList = (LinearLayout)findViewById(R.id.itemList);
        
        addDefaultItems();
        addViews();
        
        //重置按钮
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int childCount = mItemList.getChildCount();
				for(int i=0; i<childCount; i++) {
					CheckBox cb = (CheckBox)mItemList.getChildAt(i);
					cb.setEnabled(true);
					cb.setChecked(false);
				}
			}
		});
        
        //增加按钮
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BringActivity.this, AddItemActivity.class);
				BringActivity.this.startActivityForResult(intent, 0);
			}
		});
        
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BringActivity.this.finish();
			}
		});
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(mWakeLock==null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
	        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "XYTEST"); 
	        mWakeLock.acquire(); 
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mWakeLock.release();
		mWakeLock = null;
		super.onPause();
	}

	private void addViews() {
		// TODO Auto-generated method stub
		for(int i=0; i<mItems.size(); i++){
			addCheckBox(mItems.get(i));
		}
	}
	
	private void addCheckBox(String title) {
		CheckBox cb = new CheckBox(this);
		cb.setText(title);
		cb.setOnClickListener(mCBLister);
		cb.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				String title = ((CheckBox)arg0).getText().toString();
				//Remove the item from shared preference
				SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);  
		        StringBuilder sbItems = new StringBuilder(sp.getString("ITEMS", ""));
		        int start = sbItems.indexOf(title)-2;
		        sbItems.delete(start, start+title.length()+2);
		        SharedPreferences.Editor editor = sp.edit();
		        editor.putString("ITEMS", sbItems.toString());
		        editor.commit();
		        
		        mItems.remove(title);
		        mItemList.removeView(arg0);
				return true;
			}
			
		});
		mItemList.addView(cb);
	}

	private void addDefaultItems() {
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);  
        String strItems = sp.getString("ITEMS", "");  
        //在EditText中显示备忘录内容  
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
		// TODO Auto-generated method stub
		if(requestCode==0&&resultCode==0) {
			String itemName = data.getStringExtra("ITEM_NAME");
			if(mItems.contains(itemName)) {
				Toast.makeText(this, "Item "+itemName+"already exists!", Toast.LENGTH_SHORT).show();
				return;
			}
			mItems.add(itemName);
			addCheckBox(itemName);
			//获得SharedPreferences实例  
	        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);  
	        //从SharedPreferences获得备忘录的内容  
	        String strItems = sp.getString("ITEMS", "");  
			//获得编辑器  
	        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit(); 
	        //将EditText中的文本内容添加到编辑器  
	        strItems = strItems+"&&"+itemName;
	        editor.putString("ITEMS", strItems);
	        //提交编辑器内容  
	        editor.commit(); 
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private View.OnClickListener mCBLister = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			v.setEnabled(false);
		}
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mWakeLock!=null) {
			mWakeLock.release();
		}
		super.onDestroy();
	}
	
}