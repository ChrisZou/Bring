package com.zy.android.bring;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class BringList extends ArrayList<String> {
	private final String mName;
	
	public BringList(String name) {
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
