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
import ru.zaxar163.dsdmp.entities.ServerInfo;
import ru.zaxar163.dsdmp.entities.UserCache;
import ru.zaxar163.dsdmp.util.IOHelper;

public class DumpServer {
	public transient final Gson gson;

	public DumpServer(Gson gson) {
		this.gson = gson;
	}

	public void dumpChannel(ServerTextChannel ch, Path root) throws InterruptedException, IOException {
		Path write = root.resolve(ch.getIdAsString());
		DumpedServerChannel c = new DumpedServerChannel(ch);
		c.dump();
		try (BufferedWriter w = IOHelper.newWriter(write.resolve("ch.json"))) {
			gson.toJson(c, DumpedServerChannel.class, w);
		}
	}

	public void dumpChannel(ServerTextChannel ch, Path root, UserCache cache) throws InterruptedException, IOException {
		Path write = root.resolve(ch.getIdAsString());
		DumpedServerChannel c = new DumpedServerChannel(ch, cache);
		c.dump();
		try (BufferedWriter w = IOHelper.newWriter(write.resolve("ch.json"))) {
			gson.toJson(c, DumpedServerChannel.class, w);
		}
	}

	public void dumpCategory(ChannelCategory ch, Path root) throws IOException {
		Path write = root.resolve(ch.getIdAsString() + ".json");
		try (BufferedWriter w = IOHelper.newWriter(write)) {
			gson.toJson(new ChannelCategoryInfo(ch), ChannelCategoryInfo.class, w);
		}
	}

	public void dump(Server s, Path root, boolean globalUserCache, boolean checkExists)
			throws IOException, InterruptedException {
		boolean invCheckExists = !checkExists;
		ServerInfo sInfo = new ServerInfo(s);
		Path catRoot = root.resolve("categories");
		for (ChannelCategory cat : s.getChannelCategories())
			dumpCategory(cat, catRoot);
		Path channels = root.resolve("channels");
		if (globalUserCache) {
			UserCache cache = sInfo.users;
			for (ServerTextChannel c : s.getTextChannels()) {
				Path pc = channels.resolve(c.getIdAsString());
				if (!IOHelper.isDir(pc) || invCheckExists)
					dumpChannel(c, pc, cache);
			}
			try (BufferedWriter w = IOHelper.newWriter(root.resolve("server.json"))) {
				gson.toJson(sInfo, ServerInfo.class, w);
			}
		} else {
			try (BufferedWriter w = IOHelper.newWriter(root.resolve("server.json"))) {
				gson.toJson(sInfo, ServerInfo.class, w);
			}
			for (ServerTextChannel c : s.getTextChannels()) {
				Path pc = channels.resolve(c.getIdAsString());
				if (!IOHelper.isDir(pc) || invCheckExists)
					dumpChannel(c, pc);
			}
		}
	}
}
