/**
 * 
 */
package com.zy.android.dowhat.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.zy.android.dowhat.R;
import com.zy.android.dowhat.TasksActivity_;

/**
 * @author Chris
 *
 */
public class NotificationBarHelper {

	public static void showNotification(Context context, String title) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setSmallIcon(R.drawable.todo).setContentTitle("Todo").setContentText(title);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, TasksActivity_.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(TasksActivity_.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder.setOngoing(true);
		mNotificationManager.notify(0, mBuilder.build());
	}
}
