package ru.zaxar163.dsdmp.entities;

import java.util.Optional;

import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.user.User;

public class DumpedPrivateChannel extends DumpedTextChannel {
	public final transient PrivateChannel c;
	public long userId;

	public DumpedPrivateChannel() {
		super();
		c = null;
	}

	public DumpedPrivateChannel(final PrivateChannel ch) {
		super(ch);
		c = ch;
		Optional<User> u = ch.getRecipient();
		if (u.isPresent()) {
			User us = u.get();
			super.realUsers.userConsumer.accept(us);
			userId = us.getId();
		} else userId = 0;
	}

	public DumpedPrivateChannel(final PrivateChannel ch, UserCache users) {
		super(ch, users);
		c = ch;
		Optional<User> u = ch.getRecipient();
		if (u.isPresent()) {
			User us = u.get();
			super.realUsers.userConsumer.accept(us);
			userId = us.getId();
		} else userId = 0;
	}
}
