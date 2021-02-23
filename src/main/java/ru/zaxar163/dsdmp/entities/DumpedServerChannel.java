package ru.zaxar163.dsdmp.entities;

import org.javacord.api.entity.channel.ServerTextChannel;

public class DumpedServerChannel extends DumpedTextChannel {
	public final transient ServerTextChannel c;
	public String channelName;
	public String topic;
	public long date;

	public DumpedServerChannel() {
		super();
		c = null;
	}

	public DumpedServerChannel(final ServerTextChannel ch) {
		super(ch);
		this.c = ch;
		channelName = ch.getName();
		topic = ch.getTopic();
		date = ch.getCreationTimestamp().toEpochMilli();
	}

	public DumpedServerChannel(final ServerTextChannel ch, UserCache users) {
		super(ch, users);
		this.c = ch;
		channelName = ch.getName();
		topic = ch.getTopic();
		date = ch.getCreationTimestamp().toEpochMilli();
	}
}
