package com.zy.android.dowhat.beans;

import com.chriszou.androidorm.Column;
import com.chriszou.androidorm.ColumnType;
import com.chriszou.androidorm.Table;

@Table(name = "table_tags")
public class Tag {
	@Column(name = "title", type = ColumnType.STRING)
	private String title;

	@Column(name = "uuid", type = ColumnType.STRING, primaryKey = true)
	private String uuid;

	@Column(name = "_id", type = ColumnType.INTEGER, extra = "AUTOINCREMENT")
	private String id;

	public Tag(String title, String uuid, String id) {
		this.title = title;
		this.uuid = uuid;
		this.id = id;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
