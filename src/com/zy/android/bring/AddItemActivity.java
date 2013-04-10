package com.zy.android.bring;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

public class AddItemActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_dialog);
		
		//Add item button
		findViewById(R.id.confirmAdd).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String itemName = ((EditText)AddItemActivity.this.findViewById(R.id.itemTitle)).getText().toString();
				Intent i = new Intent();
				i.putExtra("ITEM_NAME", itemName);
				AddItemActivity.this.setResult(0, i);
				AddItemActivity.this.finish();
			}
		});
        
        findViewById(R.id.cancelAdd).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddItemActivity.this.setResult(-1);
				AddItemActivity.this.finish();
			}
		});
        
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        LayoutParams p = getWindow().getAttributes();
        p.width = (int)(d.getWidth()*0.7);
        getWindow().setAttributes(p);
	}
	
}