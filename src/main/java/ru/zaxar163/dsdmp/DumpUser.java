package ru.zaxar163.dsdmp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.PrivateChannel;

import com.google.gson.Gson;

import ru.zaxar163.dsdmp.entities.DumpedPrivateChannel;
import ru.zaxar163.dsdmp.entities.UserCache;
import ru.zaxar163.dsdmp.entities.UserInfo;
import ru.zaxar163.dsdmp.util.IOHelper;

public class DumpUser {
	public transient final Gson gson;

	public DumpUser(Gson gson) {
		this.gson = gson;
	}

	public void dumpChannel(PrivateChannel ch, Path root) throws InterruptedException, IOException {
		Path write = root.resolve(ch.getIdAsString());
		DumpedPrivateChannel c = new DumpedPrivateChannel(ch);
		c.dump();
		try (BufferedWriter w = IOHelper.newWriter(write.resolve("ch.json"))) {
			gson.toJson(c, DumpedPrivateChannel.class, w);
		}
	}

	public void dumpChannel(PrivateChannel ch, Path root, UserCache cache) throws InterruptedException, IOException {
		Path write = root.resolve(ch.getIdAsString());
		DumpedPrivateChannel c = new DumpedPrivateChannel(ch, cache);
		c.dump();
		try (BufferedWriter w = IOHelper.newWriter(write.resolve("ch.json"))) {
			gson.toJson(c, DumpedPrivateChannel.class, w);
		}
	}

	public void dump(DiscordApi s, Path root, boolean globalUserCache, boolean checkExists)
			throws IOException, InterruptedException {
		boolean invCheckExists = !checkExists;
		UserInfo uInfo = new UserInfo(s.getYourself());
		Path channels = root.resolve("channels");
		if (globalUserCache) {
			UserCache cache = uInfo.users;
			for (PrivateChannel c : s.getPrivateChannels()) {
				Path pc = channels.resolve(c.getIdAsString());
				if (!IOHelper.isDir(pc) || invCheckExists)
					dumpChannel(c, pc, cache);
			}
			try (BufferedWriter w = IOHelper.newWriter(root.resolve("user.json"))) {
				gson.toJson(uInfo, UserInfo.class, w);
			}
		} else {
			try (BufferedWriter w = IOHelper.newWriter(root.resolve("user.json"))) {
				gson.toJson(uInfo, UserInfo.class, w);
			}
			for (PrivateChannel c : s.getPrivateChannels()) {
				Path pc = channels.resolve(c.getIdAsString());
				if (!IOHelper.isDir(pc) || invCheckExists)
					dumpChannel(c, pc);
			}
		}
	}
}
