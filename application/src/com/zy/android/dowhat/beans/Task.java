package com.zy.android.dowhat.beans;

import java.io.Serializable;
import java.util.UUID;

import com.chriszou.androidorm.Column;
import com.chriszou.androidorm.ColumnType;
import com.chriszou.androidorm.Table;

@Table(name = "table_tasks")
public class Task implements Cloneable, Serializable {
	
	@Column(name = "title", type = ColumnType.STRING)
	private String title;

	@Column(name = "uuid", type = ColumnType.STRING)
	private String uuid;

	@Column(name = "_id", type = ColumnType.INTEGER, primaryKey = true, autoIncrement = true)
	private int id;

	public Task(String title) {
		this.title = title;
		uuid = UUID.randomUUID().toString();
	}

	public Task() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return title;
	}

	@Override
	public Task clone() throws CloneNotSupportedException {
		return (Task) super.clone();
	}
}
