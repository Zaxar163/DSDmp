package ru.zaxar163.dsdmp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.PrivateChannel;

import com.google.gson.Gson;

import ru.zaxar163.dsdmp.entities.DumpedPrivateChannel;
import ru.zaxar163.dsdmp.entities.DumpedTextChannel;
import ru.zaxar163.dsdmp.entities.UserCache;
import ru.zaxar163.dsdmp.entities.UserInfo;
import ru.zaxar163.dsdmp.util.IOHelper;

public class DumpUser extends Dump<PrivateChannel, DiscordApi, UserInfo> {

	public DumpUser(Gson gson, int messageRate, long delayMillis) {
		super(DumpedPrivateChannel.class, gson, messageRate, delayMillis);
	}

	@Override
	protected Iterable<PrivateChannel> iterateOver(DiscordApi from) {
		return from.getPrivateChannels();
	}

	@Override
	protected void postDump(UserInfo to, Path root) throws InterruptedException, IOException {
		try (BufferedWriter w = IOHelper.newWriter(root.resolve("user.json"))) {
			gson.toJson(to, UserInfo.class, w);
		}
	}

	@Override
	protected UserInfo preDump(DiscordApi from, Path root) throws InterruptedException, IOException {
		return new UserInfo(from.getYourself());
	}

	@Override
	protected DumpedTextChannel textChannel(PrivateChannel from, UserCache optional) {
		return optional == null ? new DumpedPrivateChannel(from) : new DumpedPrivateChannel(from, optional);
	}
}
