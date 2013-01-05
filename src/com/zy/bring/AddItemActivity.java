package com.zy.bring;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

public class AddItemActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_dialog);
		
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
		LayoutParams p = getWindow().getAttributes();
		p.width = (int) (metric.widthPixels * 0.8);
		getWindow().setAttributes(p);
		
		//Add an item
        findViewById(R.id.confirmAdd).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String itemName = ((EditText)AddItemActivity.this.findViewById(R.id.itemTitle)).getText().toString();
				Intent i = new Intent();
				i.putExtra(Const.EXTRA_ITEM_NAME, itemName);
				setResult(RESULT_OK, i);
				finish();
			}
		});
        
        findViewById(R.id.cancelAdd).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
	
}
