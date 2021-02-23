package ru.zaxar163.dsdmp.run;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.zaxar163.dsdmp.DumpServer;
import ru.zaxar163.dsdmp.util.IOHelper;

public class DSServerDumper {
	private static boolean getBoolArg(int number, String[] args, boolean defaultValue) {
		return args.length > number ? Boolean.parseBoolean(args[number]) : defaultValue;
	}

	public static void main(final String[] args) throws IOException {
		if (args.length < 3) {
			System.out.println(
					"Usage: dumpServer <dumpFolder> <token> <serverId> [prettyPrint] [globalUserCache] [checkExists]");
			System.exit(1);
		}
		boolean prettyPrint = getBoolArg(3, args, true);
		GsonBuilder jsB = new GsonBuilder().enableComplexMapKeySerialization();
		if (prettyPrint)
			jsB = jsB.setPrettyPrinting();
		Gson gson = jsB.create();
		String str = "Bot ";
		boolean client = !args[1].startsWith(str);
		if (!client)
			args[1] = args[1].substring(str.length());
		final DiscordApi api = new DiscordApiBuilder().setWaitForServersOnStartup(false).setToken(args[1])
				.setAccountType(client ? AccountType.CLIENT : AccountType.BOT).login().join();
		System.out.println("Logged as: " + api.getYourself().getDiscriminatedName() + " " + api.getYourself().getId());
		final Path p = IOHelper.toAbsPath(Paths.get(args[0])).normalize();
		DumpServer u = new DumpServer(gson);
		try {
			u.dump(api.getServerById(args[2]).orElseThrow(() -> new RuntimeException("Invalid server id " + args[2])),
					p, getBoolArg(4, args, false), getBoolArg(5, args, true));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace(System.err);
			api.disconnect();
			System.exit(2);
		}
		api.disconnect();
	}
}
