package com.zy.android.dowhat;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class TaskList extends ArrayList<String> {
	private String mName;
	
	public TaskList(String name) {
		this.mName = name;
	}
	
	public void setName(String name) {
		this.mName = name;
	}

	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return mName;
	}
	
}
