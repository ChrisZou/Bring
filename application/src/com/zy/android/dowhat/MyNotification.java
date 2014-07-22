package com.zy.android.dowhat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyNotification extends Notification {

	private Context ctx;
	private NotificationManager mNotificationManager;

	public MyNotification(Context ctx) {
		super();
		this.ctx = ctx;
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
		CharSequence tickerText = "Shortcuts";
		long when = System.currentTimeMillis();
		Notification.Builder builder = new Notification.Builder(ctx);
		Notification notification = builder.getNotification();
		notification.when = when;
		notification.tickerText = tickerText;
		notification.icon = R.drawable.ic_launcher;

		RemoteViews contentView = new RemoteViews(ctx.getPackageName(), R.layout.notification_layout);

		// set the button listeners
		setListeners(contentView);

		notification.contentView = contentView;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(548853, notification);
	}

	public void setListeners(RemoteViews view) {
		// radio listener
		Intent radio = new Intent(ctx, TasksActivity_.class);
		radio.putExtra("DO", "radio");
		PendingIntent pRadio = PendingIntent.getActivity(ctx, 0, radio, 0);
		view.setOnClickPendingIntent(R.id.radio, pRadio);

		// volume listener
		Intent volume = new Intent(ctx, TasksActivity_.class);
		volume.putExtra("DO", "volume");
		PendingIntent pVolume = PendingIntent.getActivity(ctx, 1, volume, 0);
		view.setOnClickPendingIntent(R.id.volume, pVolume);

		// reboot listener
		Intent reboot = new Intent(ctx, TasksActivity_.class);
		reboot.putExtra("DO", "reboot");
		PendingIntent pReboot = PendingIntent.getActivity(ctx, 5, reboot, 0);
		view.setOnClickPendingIntent(R.id.reboot, pReboot);

		// app listener
		Intent app = new Intent(ctx, TasksActivity_.class);
		app.putExtra("DO", "app");
		PendingIntent pApp = PendingIntent.getActivity(ctx, 4, app, 0);
		view.setOnClickPendingIntent(R.id.app, pApp);
	}

}
