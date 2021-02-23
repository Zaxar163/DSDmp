package ru.zaxar163.dsdmp.entities;

import java.util.List;
import java.util.stream.Collectors;

import org.javacord.api.entity.channel.ChannelCategory;

public class ChannelCategoryInfo {
	public String name;
	public long date, id;
	public int position;
	public List<Long> channels;

	public ChannelCategoryInfo() {
	}

	public ChannelCategoryInfo(ChannelCategory category) {
		name = category.getName();
		date = category.getCreationTimestamp().toEpochMilli();
		id = category.getId();
		position = category.getRawPosition();
		channels = category.getChannels().stream().map(e -> e.getId()).collect(Collectors.toList());
	}
}
