package com.zy.android.dowhat.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ConfirmDialog {
	private AlertDialog mDialog;


	public ConfirmDialog(Context context, String msg, final OkAction action) {
		this(context, null, msg, action);
	}
	
	public ConfirmDialog(Context context, String title, String msg, final OkAction action) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(msg);
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				action.act();
			}
		});

		mDialog = builder.create();
	}
	
	public AlertDialog getDialog() {
		return mDialog;
	}

	public void show() {
		mDialog.show();
	}

	public static interface OkAction {
		public void act();
	}
}
