package ru.zaxar163.dsdmp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.javacord.api.entity.channel.TextChannel;

import com.google.gson.Gson;

import ru.zaxar163.dsdmp.entities.CommonInfo;
import ru.zaxar163.dsdmp.entities.DumpedTextChannel;
import ru.zaxar163.dsdmp.entities.UserCache;
import ru.zaxar163.dsdmp.util.IOHelper;

public abstract class Dump<T extends TextChannel, B, C extends CommonInfo> {
	public transient final Gson gson;
	public transient final int messageRate;
	public transient final long delayMillis;
	private transient final Class<?> serClass;

	public Dump(Class<?> serClass, Gson gson, int messageRate, long delayMillis) {
		this.serClass = serClass;
		this.gson = gson;
		this.messageRate = messageRate;
		this.delayMillis = delayMillis;
	}

	private void baseDump(B s, C info, Function<Iterable<T>, Iterable<T>> consumer,
			BiConsumer<LogKind, Long> infoAcceptor, Path root, boolean overwrite, UserCache cache)
			throws InterruptedException, IOException {
		Path channels = root.resolve("channels");
		Iterable<T> chs = consumer.apply(iterateOver(s));
		for (T c : chs) {
			infoAcceptor.accept(LogKind.DUMP_START, c.getId());
			Path pc = channels.resolve(c.getIdAsString());
			if (!IOHelper.isDir(pc) || overwrite)
				infoAcceptor.accept(LogKind.MESSAGES, dumpChannel(c, pc, cache));
			infoAcceptor.accept(LogKind.DUMP_END, c.getId());
		}
	}

	public void dump(B s, Function<Iterable<T>, Iterable<T>> consumer, BiConsumer<LogKind, Long> infoAcceptor,
			Path root, boolean globalUserCache, boolean overwrite) throws IOException, InterruptedException {
		C info = preDump(s, root);
		if (globalUserCache) {
			baseDump(s, info, consumer, infoAcceptor, root, overwrite, info.users);
			postDump(info, root);
		} else {
			postDump(info, root);
			baseDump(s, info, consumer, infoAcceptor, root, overwrite, null);
		}
	}

	public long dumpChannel(T ch, Path root) throws InterruptedException, IOException {
		return dumpChannel(ch, root, null);
	}

	public long dumpChannel(T ch, Path root, UserCache cache) throws InterruptedException, IOException {
		DumpedTextChannel c = textChannel(ch, cache);
		write(root, c);
		return c.messages.size();
	}

	protected abstract Iterable<T> iterateOver(B from);

	protected abstract void postDump(C to, Path root) throws InterruptedException, IOException;

	protected abstract C preDump(B from, Path root) throws InterruptedException, IOException;

	protected abstract DumpedTextChannel textChannel(T from, UserCache optional);

	private void write(Path root, DumpedTextChannel c) throws InterruptedException, IOException {
		c.dump(messageRate, delayMillis);
		try (BufferedWriter w = IOHelper.newWriter(root.resolve("ch.json"))) {
			gson.toJson(c, serClass, w);
		}
	}
}
