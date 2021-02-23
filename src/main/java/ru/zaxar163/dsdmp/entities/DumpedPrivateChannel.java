package ru.zaxar163.dsdmp.entities;

import org.javacord.api.entity.channel.PrivateChannel;

public class DumpedPrivateChannel extends DumpedTextChannel {
	public final transient PrivateChannel c;
	public long userId;
	public String userName;

	public DumpedPrivateChannel() {
		super();
		c = null;
	}

	public DumpedPrivateChannel(final PrivateChannel ch) {
		super(ch);
		this.c = ch;
		userName = ch.getRecipient().getDiscriminatedName();
		userId = ch.getRecipient().getId();
	}

	public DumpedPrivateChannel(final PrivateChannel ch, UserCache users) {
		super(ch, users);
		this.c = ch;
		userName = ch.getRecipient().getDiscriminatedName();
		userId = ch.getRecipient().getId();
	}
}
