package ru.zaxar163.dsdmp.run;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;

import com.google.gson.Gson;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import ru.zaxar163.dsdmp.Dump;
import ru.zaxar163.dsdmp.entities.CommonInfo;
import ru.zaxar163.dsdmp.util.DSUtil;

public final class DSDump {
	@FunctionalInterface
	public interface DumperConsumer<T extends TextChannel, P, D extends CommonInfo> {
		Dump<T, P, D> dumper(Gson json, int messageRate, long delayMillis);
	}

	public static final int DEFAULT_MESSAGE_RATE = 100;
	public static final long DEFAULT_DELAY = 100;

	private static <T extends TextChannel> Function<Iterable<T>, Iterable<T>> consumer(Long idCheck) {
		long id = idCheck;
		return e -> () -> StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(e.iterator(), Spliterator.NONNULL), false)
				.filter(f -> f.getId() == id).iterator();
	}

	public static <T extends TextChannel, P, D extends CommonInfo> void doDump(Consumer<OptionParser> opts,
			BiFunction<DiscordApi, OptionSet, P> argProvider, DumperConsumer<T, P, D> dmp, String[] args)
			throws IOException, InterruptedException {
		OptionParser base = DSUtil.discord();
		base.accepts("globalCache", "Globally store info about users (id name pairs)");
		base.accepts("messageRate",
				"Amount of messages, which dumper get on every request (default " + DEFAULT_MESSAGE_RATE + ")")
				.withRequiredArg().ofType(int.class).defaultsTo(DEFAULT_MESSAGE_RATE);
		base.accepts("requestDelay", "Delay between requests (default " + DEFAULT_DELAY + ")").withRequiredArg()
				.ofType(long.class).defaultsTo(DEFAULT_DELAY);
		base.accepts("channelId", "Id of channel to dump (if not specified all channels will be dumped)")
				.withRequiredArg().ofType(long.class);
		opts.accept(base);
		OptionSet parsed;
		if ((parsed = DSUtil.tryPrintHelp(args, base)) == null) {
			System.exit(1);
			return;
		}
		DiscordApi api = DSUtil.init(parsed);
		Dump<T, P, D> dumper = dmp.dumper(DSUtil.JSON, (Integer) parsed.valueOf("messageRate"),
				(Long) parsed.valueOf("requestDelay"));
		dumper.dump(argProvider.apply(api, parsed),
				parsed.has("channelId") ? consumer((Long) parsed.valueOf("channelId")) : e -> e, (a, b) -> {
					System.out.print(a.append);
					System.out.println(b);
				}, (Path) parsed.valueOf("dir"), parsed.has("globalCache"), parsed.has("overwrite"));
	}

	private DSDump() {
	}
}
