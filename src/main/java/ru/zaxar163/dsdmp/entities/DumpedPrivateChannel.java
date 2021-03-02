package ru.zaxar163.dsdmp.entities;

import org.javacord.api.entity.channel.PrivateChannel;

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
		super.realUsers.userConsumer.accept(ch.getRecipient());
		userId = ch.getRecipient().getId();
	}

	public DumpedPrivateChannel(final PrivateChannel ch, UserCache users) {
		super(ch, users);
		c = ch;
		super.realUsers.userConsumer.accept(ch.getRecipient());
		userId = ch.getRecipient().getId();
	}
}
