package ru.zaxar163.dsdmp.entities;

import org.javacord.api.entity.user.User;

public class UserInfo extends CommonInfo {

	public UserInfo() {
	}

	public UserInfo(User u) {
		id = u.getId();
		date = u.getCreationTimestamp().toEpochMilli();
		users = new UserCache();
		users.userConsumer.accept(u);
	}
}
