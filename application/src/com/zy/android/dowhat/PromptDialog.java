package com.zy.android.dowhat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity
public class PromptDialog extends Activity {
	
	public static final String EXTRA_STRING_TITLE = "extra_title";
	public static final String EXTRA_STRING_TIP = "extra_tip";
	public static final String EXTRA_RESULT = "extra_result";

	@ViewById(R.id.itemTitle)
	EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_dialog);

		String title = getIntent().getStringExtra(EXTRA_STRING_TITLE);
        if(title!=null) {
        	setTitle(title);
        }
        setWindowWidth();
    }

	@AfterViews
	void initViews() {
		String tip = getIntent().getStringExtra(EXTRA_STRING_TIP);
		if (tip != null) {
			mEditText.setText(tip);
		}
	}

    private void setWindowWidth() {
        LayoutParams p = getWindow().getAttributes();
		p.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        getWindow().setAttributes(p);
    }

    @Click(R.id.prompt_dialog_ok)
	void onOk() {
        String itemName = ((EditText) findViewById(R.id.itemTitle)).getText().toString().trim();
        if (itemName.length() == 0) {
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_RESULT, itemName);
        setResult(RESULT_OK, i);
        finish();
    }

	@Click(R.id.prompt_dialog_cancel)
    void cancelAdding() {
        setResult(RESULT_CANCELED);
        finish();
    }
}