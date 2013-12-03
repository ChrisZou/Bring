package com.zy.android.dowhat;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class BringList extends ArrayList<String> {
	private String mName;
	
	public BringList(String name) {
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
