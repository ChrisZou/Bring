package com.zy.android.dowhat.beans;

import java.util.UUID;

import com.chriszou.androidorm.Column;
import com.chriszou.androidorm.ColumnType;
import com.chriszou.androidorm.Table;

@Table(name = "table_task_tags")
public class TaskTag implements Cloneable {

	@Column(name = "_id", type = ColumnType.INTEGER, autoIncrement = true, primaryKey = true)
	private int id;

	@Column(name = "uuid", type = ColumnType.STRING, notNull = true)
	private String uuid;

	@Column(name = "task_uuid", type = ColumnType.STRING, notNull = true)
	private String taskUuid;

	@Column(name = "tag_uuid", type = ColumnType.STRING, notNull = true)
	private String tagUuid;

	private TaskTag(String taskUuid, String tagUuid) {
		this.taskUuid = taskUuid;
		this.tagUuid = tagUuid;
		uuid = UUID.randomUUID().toString();
	}

	public TaskTag(Task task, Tag tag) {
		this(task.getUuid(), tag.getUuid());
	}
	
	public TaskTag() { }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTaskUuid() {
		return taskUuid;
	}

	public void setTaskUuid(String taskUuid) {
		this.taskUuid = taskUuid;
	}

	public String getTagUuid() {
		return tagUuid;
	}

	public void setTagUuid(String tagUuid) {
		this.tagUuid = tagUuid;
	}

	@Override
	protected TaskTag clone() throws CloneNotSupportedException {
		return (TaskTag) super.clone();
	}
}
