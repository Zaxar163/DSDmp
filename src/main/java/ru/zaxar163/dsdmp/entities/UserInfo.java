package ru.zaxar163.dsdmp.entities;

import org.javacord.api.entity.user.User;

public class UserInfo {
	public long id, date;
	public String discriminatedName;
	public UserCache users;

	public UserInfo() {
	}

	public UserInfo(User u) {
		id = u.getId();
		date = u.getCreationTimestamp().toEpochMilli();
		discriminatedName = u.getDiscriminatedName();
		users = new UserCache();
	}
}
