package ru.zaxar163.dsdmp.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javacord.api.DiscordApi;

public class UserGlobalInfo extends UserInfo {
	public List<ServerInfo> servers;
	public List<DumpedPrivateChannel> privateChannels;
	public List<DumpedGroupChannel> groupChannels;

	public UserGlobalInfo() {
		servers = new ArrayList<>();
		privateChannels = new ArrayList<>();
		groupChannels = new ArrayList<>();
	}

	public UserGlobalInfo(DiscordApi u, boolean dumpServers) {
		super(u.getYourself());
		privateChannels = u.getPrivateChannels().stream().map(e -> new DumpedPrivateChannel(e, users))
				.collect(Collectors.toList());
		groupChannels = u.getGroupChannels().stream().map(e -> new DumpedGroupChannel(e, users))
				.collect(Collectors.toList());
		servers = dumpServers ? u.getServers().stream().map(e -> new ServerInfo(e)).collect(Collectors.toList())
				: new ArrayList<>();
	}
}
