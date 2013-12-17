package com.zy.android.dowhat.beans;

import com.chriszou.androidorm.Column;
import com.chriszou.androidorm.ColumnType;
import com.chriszou.androidorm.Table;

@Table(name = "table_task_tags")
public class TaskTag {

	@Column(name = "_id", type = ColumnType.INTEGER, autoIncrement = true, primaryKey = true)
	private int id;

	@Column(name = "uuid", type = ColumnType.STRING, notNull = true)
	private String uuid;

	@Column(name = "task_uuid", type = ColumnType.STRING, notNull = true)
	private String taskUuid;

	@Column(name = "tag_uuid", type = ColumnType.STRING, notNull = true)
	private String tagUuid;
}
