package com.zy.android.dowhat.model;

import android.content.Context;

import com.zy.android.dowhat.beans.Tag;

public class TagModel extends Model<Tag> {

	private volatile static TagModel singleInstance;

	private TagModel(Context ctx) {
		super(ctx, Tag.class);
	}

	public static TagModel getInstance(Context ctx) {
		if (singleInstance == null) {
			synchronized (TagModel.class) {
				if (singleInstance == null) {
					singleInstance = new TagModel(ctx);
				}
			}
		}
		return singleInstance;
	}

	public Tag getTagFromUuid(String tagUuid) {
		for (Tag tag : copyAll()) {
			if (tag.getUuid().equals(tagUuid)) {
				return tag;
			}
		}
		return null;
	}
}
