package com.zy.android.bring;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

public class AddItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dialog);

        initViews();
        setWindowWidth();
    }

    private void initViews() {
        // Add item button
        findViewById(R.id.confirmAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        findViewById(R.id.cancelAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAdding();
            }
        });
    }

    private void setWindowWidth() {
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        LayoutParams p = getWindow().getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        getWindow().setAttributes(p);
    }

    private void addItem() {
        String itemName = ((EditText) findViewById(R.id.itemTitle)).getText().toString().trim();
        if (itemName.length() == 0) {
            return;
        }

        Intent i = new Intent();
        i.putExtra("ITEM_NAME", itemName);
        setResult(RESULT_OK, i);
        finish();
    }

    private void cancelAdding() {
        setResult(RESULT_CANCELED);
        finish();
    }
}