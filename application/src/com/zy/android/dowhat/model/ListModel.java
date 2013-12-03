package com.zy.android.dowhat.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.zy.android.dowhat.BringList;
import com.zy.android.dowhat.utils.L;

public class ListModel {

	public static final String ITEM_SPLITER = "::";
	public static final String PREF_STRING_LIST_ = "pref_string_list_";
	public static final String PREF_STRING_LISTS = "pref_string_list_names";

	private volatile static ListModel singleInstance;
	private SharedPreferences mPreferences;

	private final List<BringList> mLists;

	private ListModel(Context ctx) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		mLists = new ArrayList<BringList>();
		init();
	}

	private void init() {
		String strLists = mPreferences.getString(PREF_STRING_LISTS, "");
		String[] lists = strLists.split(ITEM_SPLITER);
		for (String list : lists) {
			if (list.length() > 0) {
				mLists.add(getList(list));
			}
		}
	}

	public static ListModel getInstance(Context ctx) {
		if (singleInstance == null) {
			synchronized (ListModel.class) {
				if (singleInstance == null) {
					singleInstance = new ListModel(ctx);
				}
			}
		}
		return singleInstance;
	}

	public List<BringList> getAllLists() {
		return mLists;
	}
	
	public void addList(String name) {
		if (hasList(name)) {
			return;
		}

		mLists.add(new BringList(name));
		saveLists();
	}

	public void renameList(BringList list, String newName) {
		mPreferences.edit().remove(PREF_STRING_LIST_ + list.getName()).commit();
		list.setName(newName);
		saveList(list);
		saveLists();
	}

	public void removeList(BringList list) {
		mLists.remove(list);
		mPreferences.edit().remove(PREF_STRING_LIST_ + list.getName()).commit();
		saveLists();
	}

	public void saveLists() {
		String lists = TextUtils.join(ITEM_SPLITER, mLists);
		mPreferences.edit().putString(PREF_STRING_LISTS, lists).commit();
	}

	public BringList getList(String name){
        String strItems = mPreferences.getString(PREF_STRING_LIST_+name, "");
        String[] arrItems = strItems.split(ITEM_SPLITER);
        BringList list = new BringList(name);
        for (String str : arrItems) {
            if (str != null && !(str.trim().length() == 0)) {
                list.add(str);
            }
        }
        return list;
	}

	public boolean hasList(String name){
		for(BringList list:mLists) {
			if(list.getName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}

	public void saveList(BringList list) {
		String items = TextUtils.join(ITEM_SPLITER, list);
		mPreferences.edit().putString(PREF_STRING_LIST_ + list.getName(), items).commit();
		L.l("save list");
	}
}
