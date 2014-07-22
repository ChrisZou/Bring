package com.zy.android.dowhat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zy.android.dowhat.model.TaskModel;
import com.zy.android.dowhat.utils.NotificationBarHelper;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(TaskModel.getInstance(context).getCount()>0) {
			NotificationBarHelper.showNotification(context, TaskModel.getInstance(context).copyAll().get(0).getTitle());
		}
	}
}