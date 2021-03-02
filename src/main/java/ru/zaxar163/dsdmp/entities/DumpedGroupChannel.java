package ru.zaxar163.dsdmp.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javacord.api.entity.channel.GroupChannel;

public class DumpedGroupChannel extends DumpedTextChannel {
	public final transient GroupChannel c;
	public List<Long> members;
	public String name;

	public DumpedGroupChannel() {
		super();
		c = null;
		name = null;
		members = new ArrayList<>();
	}

	public DumpedGroupChannel(final GroupChannel ch) {
		super(ch);
		c = ch;
		name = ch.getName().orElse(null);
		ch.getMembers().forEach(super.realUsers.userConsumer);
		members = ch.getMembers().stream().map(e -> e.getId()).collect(Collectors.toList());
	}

	public DumpedGroupChannel(final GroupChannel ch, UserCache users) {
		super(ch, users);
		c = ch;
		name = ch.getName().orElse(null);
		ch.getMembers().forEach(super.realUsers.userConsumer);
		members = ch.getMembers().stream().map(e -> e.getId()).collect(Collectors.toList());
	}
}
