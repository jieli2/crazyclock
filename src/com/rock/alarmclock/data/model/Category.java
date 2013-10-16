package com.rock.alarmclock.data.model;

import java.util.ArrayList;
import java.util.List;

public class Category {

	private String name;
	private List<Channel> channellist;

	public Category() {
		channellist = new ArrayList<Channel>();
	}

	public Category(String name, List<Channel> list) {
		this.channellist = list;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Channel> getChannellist() {
		return channellist;
	}

	public void setChannellist(List<Channel> channellist) {
		this.channellist = channellist;
	}

	public void addChannel(Channel c) {
		if (channellist != null)
			channellist.add(c);
	}

}
