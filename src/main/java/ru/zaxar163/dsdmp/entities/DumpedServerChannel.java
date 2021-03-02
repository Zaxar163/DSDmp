package ru.zaxar163.dsdmp.entities;

import org.javacord.api.entity.channel.ServerTextChannel;

public class DumpedServerChannel extends DumpedTextChannel {
	public final transient ServerTextChannel c;
	public String channelName;
	public String topic;

	public DumpedServerChannel() {
		super();
		c = null;
		channelName = topic = null;
	}

	public DumpedServerChannel(final ServerTextChannel ch) {
		super(ch);
		c = ch;
		channelName = ch.getName();
		topic = ch.getTopic();
	}

	public DumpedServerChannel(final ServerTextChannel ch, UserCache users) {
		super(ch, users);
		c = ch;
		channelName = ch.getName();
		topic = ch.getTopic();
	}
}
