package com.zy.bring;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class BringActivity extends Activity {
	
	private List<String> mItems = new ArrayList<String>();
	private LinearLayout mItemList = null;
	private PowerManager.WakeLock mWakeLock;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "XYTEST"); 
        mWakeLock.acquire(); 
        
        mItemList = (LinearLayout)findViewById(R.id.itemList);
         
        addDefaultItems();
        addViews();
        
        //���ð�ť
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
        
        //���Ӱ�ť
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
	protected void onPause() {
		// TODO Auto-generated method stub
		mWakeLock.release();
		mWakeLock = null;
		super.onPause();
	}

	private void addViews() {
		// TODO Auto-generated method stub
		for(int i=0; i<mItems.size(); i++){
			CheckBox cb = new CheckBox(this);
			cb.setText(mItems.get(i));
			cb.setOnClickListener(mCBLister);
			mItemList.addView(cb);
		}
	}

	private void addDefaultItems() {
		// TODO Auto-generated method stub
		mItems.add("�ֻ�");
		mItems.add("Կ��");
		mItems.add("Ǯ��");
		mItems.add("ֽ��");
		mItems.add("����");
		mItems.add("�ֱ�");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==0&&resultCode==0) {
			String itemName = data.getStringExtra("ITEM_NAME");
			mItems.add(itemName);
			CheckBox cb = new CheckBox(this);
			cb.setText(itemName);
			cb.setOnClickListener(mCBLister);
			mItemList.addView(cb);
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