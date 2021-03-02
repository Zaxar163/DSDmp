package ru.zaxar163.dsdmp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;

import com.google.gson.Gson;

import ru.zaxar163.dsdmp.entities.ChannelCategoryInfo;
import ru.zaxar163.dsdmp.entities.DumpedServerChannel;
import ru.zaxar163.dsdmp.entities.DumpedTextChannel;
import ru.zaxar163.dsdmp.entities.ServerInfo;
import ru.zaxar163.dsdmp.entities.UserCache;
import ru.zaxar163.dsdmp.util.IOHelper;

public class DumpServer extends Dump<ServerTextChannel, Server, ServerInfo> {
	public DumpServer(Gson gson, int messageRate, long delayMillis) {
		super(DumpedServerChannel.class, gson, messageRate, delayMillis);
	}

	public void dumpCategory(ChannelCategory ch, Path root) throws IOException {
		Path write = root.resolve(ch.getIdAsString() + ".json");
		try (BufferedWriter w = IOHelper.newWriter(write)) {
			gson.toJson(new ChannelCategoryInfo(ch), ChannelCategoryInfo.class, w);
		}
	}

	@Override
	protected Iterable<ServerTextChannel> iterateOver(Server from) {
		return from.getTextChannels();
	}

	@Override
	protected void postDump(ServerInfo to, Path root) throws InterruptedException, IOException {
		try (BufferedWriter w = IOHelper.newWriter(root.resolve("server.json"))) {
			gson.toJson(to, ServerInfo.class, w);
		}
	}

	@Override
	protected ServerInfo preDump(Server from, Path root) throws InterruptedException, IOException {
		ServerInfo sInfo = new ServerInfo(from);
		Path catRoot = root.resolve("categories");
		for (ChannelCategory cat : from.getChannelCategories())
			dumpCategory(cat, catRoot);
		return sInfo;
	}

	@Override
	protected DumpedTextChannel textChannel(ServerTextChannel from, UserCache optional) {
		return optional == null ? new DumpedServerChannel(from) : new DumpedServerChannel(from, optional);
	}
}
