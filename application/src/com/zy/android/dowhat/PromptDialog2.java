package com.zy.android.dowhat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PromptDialog2 {
	private AlertDialog mDialog;

	public PromptDialog2(Context context, String title, String tip, final OnOkListener action) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		final View view = LayoutInflater.from(context).inflate(R.layout.prompt_dialog_layout, null);
		final EditText editText = (EditText) view.findViewById(R.id.prompt_dialog_edit);
		editText.setText(tip);
		builder.setView(view);
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				action.onOk(editText.getText().toString());
			}
		});
		mDialog = builder.create();
	}

	public void show() {
		mDialog.show();
	}

	public static interface OnOkListener {
		public void onOk(String msg);
	}
}
