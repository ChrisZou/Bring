package com.zy.android.dowhat.beans;

import com.chriszou.androidorm.Column;
import com.chriszou.androidorm.ColumnType;

public class TaskTag {

	@Column(name = "_id", type = ColumnType.INTEGER, autoIncrement = true)
	private int id;

	@Column(name = "uuid", type = ColumnType.STRING, notNull = true, primaryKey = true)
	private String uuid;

	@Column(name = "task_uuid", type = ColumnType.STRING, notNull = true)
	private String taskUuid;

	@Column(name = "tag_uuid", type = ColumnType.STRING, notNull = true)
	private String tagUuid;
}
