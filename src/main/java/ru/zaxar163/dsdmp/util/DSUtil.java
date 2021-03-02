package ru.zaxar163.dsdmp.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public final class DSUtil {
	public static final String BOT_ACCOUNT_PREFIX = "Bot ";
	public static final boolean WAIT_FOR_SERVERS = Boolean
			.parseBoolean(System.getProperty("discord.waitForServersOnStartup", "false"));
	public static final boolean PRETTY_PRINT = Boolean.parseBoolean(System.getProperty("discord.prettyPrint", "true"));
	public static final Gson JSON = (PRETTY_PRINT ? new GsonBuilder().setPrettyPrinting() : new GsonBuilder())
			.enableComplexMapKeySerialization().create();

	public static OptionParser base() {
		OptionParser parser = new OptionParser();
		parser.accepts("dir", "Dump directory").withRequiredArg().required().withValuesConvertedBy(path());
		parser.accepts("overwrite", "Overwrite if exists");
		parser.accepts("help", "Display help").forHelp();
		return parser;
	}

	public static OptionParser discord() {
		OptionParser parser = base();
		parser.accepts("token", "Discord token").withRequiredArg().required();
		return parser;
	}

	public static DiscordApi init(OptionSet token) {
		return init(token.valueOf("token").toString());
	}

	public static DiscordApi init(String token) {
		final boolean bot = token.startsWith(BOT_ACCOUNT_PREFIX);
		if (bot)
			token = token.substring(BOT_ACCOUNT_PREFIX.length());
		return new DiscordApiBuilder().setWaitForServersOnStartup(WAIT_FOR_SERVERS).setToken(token)
				.setAccountType(bot ? AccountType.BOT : AccountType.CLIENT).login().join();
	}

	public static ValueConverter<Path> path() {
		return new ValueConverter<Path>() {
			@Override
			public Path convert(String value) {
				Path path = Paths.get(value);

				boolean good = Files.notExists(path) || Files.isDirectory(path);
				if (!good)
					throw new ValueConversionException("File " + path.toString() + "exists but it is not directory");
				return path;
			}

			@Override
			public String valuePattern() {
				return null;
			}

			@Override
			public Class<Path> valueType() {
				return Path.class;
			}
		};
	}

	private static void printHelp(OptionParser parser) {
		try {
			parser.printHelpOn(new Writer() {
				private StringBuffer sb = new StringBuffer();

				@Override
				public void close() throws IOException {
				}

				@Override
				public void flush() throws IOException {
					System.out.flush();
				}

				@Override
				public void write(char[] cbuf, int off, int len) throws IOException {
					sb.setLength(0);
					sb.append(cbuf, off, len);
					System.out.append(sb);
				}

				@Override
				public void write(int c) throws IOException {
					System.out.append((char) c);
				}

				@Override
				public void write(String str, int off, int len) throws IOException {
					System.out.append(str, off, len);
				}

			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static OptionSet tryPrintHelp(String[] args, OptionParser parser) {
		if (args.length == 0) {
			printHelp(parser);
			return null;
		}
		OptionSet s = parser.parse(args);
		if (s.has("help")) {
			printHelp(parser);
			return null;
		}
		return s;
	}

	private DSUtil() {
	}
}
