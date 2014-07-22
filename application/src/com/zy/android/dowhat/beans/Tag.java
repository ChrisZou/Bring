package com.zy.android.dowhat.beans;

import java.util.UUID;

import com.chriszou.androidorm.Column;
import com.chriszou.androidorm.ColumnType;
import com.chriszou.androidorm.Table;

@Table(name = "table_tags")
public class Tag implements Cloneable {
	@Column(name = "title", type = ColumnType.STRING)
	private String title;

	@Column(name = "uuid", type = ColumnType.STRING)
	private String uuid;

	@Column(name = "_id", type = ColumnType.INTEGER, primaryKey = true, autoIncrement = true)
	private int id;

	public static final Tag sNoTag = new Tag("No Tag");
	public static final Tag sAll = new Tag("All");

	public Tag(String title, String uuid, int id) {
		this.title = title;
		this.uuid = uuid;
		this.id = id;
	}

	public Tag() {
	}

	public Tag(String title) {
		this.title = title;
		uuid = UUID.randomUUID().toString();
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
	public Tag clone() throws CloneNotSupportedException {
		return (Tag) super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Tag) {
			Tag tag = (Tag) o;
			if (tag.getTitle().equals(this.getTitle()) && tag.getUuid().equals(this.getUuid())) {
				return true;
			}
		}
		return false;
	}

}
