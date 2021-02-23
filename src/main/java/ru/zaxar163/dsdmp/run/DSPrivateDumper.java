package ru.zaxar163.dsdmp.run;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.zaxar163.dsdmp.DumpUser;
import ru.zaxar163.dsdmp.util.IOHelper;

public class DSPrivateDumper {
	private static boolean getBoolArg(int number, String[] args, boolean defaultValue) {
		return args.length > number ? Boolean.parseBoolean(args[number]) : defaultValue;
	}

	public static void main(final String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Usage: dumpPrivate <dumpFolder> <token> [prettyPrint] [globalUserCache] [checkExists]");
			System.exit(1);
		}
		boolean prettyPrint = getBoolArg(2, args, true);
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
		DumpUser u = new DumpUser(gson);
		try {
			u.dump(api, p, getBoolArg(3, args, false), getBoolArg(4, args, true));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace(System.err);
			api.disconnect();
			System.exit(2);
		}
		api.disconnect();
	}
}
