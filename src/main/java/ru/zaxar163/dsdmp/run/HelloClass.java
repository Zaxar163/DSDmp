package ru.zaxar163.dsdmp.run;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.javacord.api.DiscordApi;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import ru.zaxar163.dsdmp.DumpGroup;
import ru.zaxar163.dsdmp.DumpServer;
import ru.zaxar163.dsdmp.DumpUser;
import ru.zaxar163.dsdmp.entities.UserGlobalInfo;
import ru.zaxar163.dsdmp.util.DSUtil;
import ru.zaxar163.dsdmp.util.IOHelper;

public final class HelloClass {
	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length < 1) {
			System.out.println("Modes: dumpPrivate, dumpServer, dumpGroup, listInfo, fetchAtt");
			System.out.println("Advice: set max memory (-Xmx) 512M or more");
			System.exit(1);
		}
		String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
		switch (args[0]) {
		case "dumpPrivate":
			System.out.println("Mode: dumpPrivate");
			DSDump.doDump(opts -> {
			}, (a, b) -> a, DumpUser::new, realArgs);
			break;
		case "dumpServer":
			System.out.println("Mode: dumpServer");
			DSDump.doDump(
					opts -> opts.accepts("serverId", "Id of server to dump").withRequiredArg().required()
							.ofType(long.class),
					(a, b) -> a.getServerById(b.valueOf("serverId").toString())
							.orElseThrow(() -> new IllegalArgumentException(
									"Server with id " + b.valueOf("serverId") + " not found!")),
					DumpServer::new, realArgs);
			break;
		case "dumpGroup":
			System.out.println("Mode: dumpGroup");
			DSDump.doDump(opts -> {
			}, (a, b) -> a, DumpGroup::new, realArgs);
			break;
		case "listInfo":
			System.out.println("Mode: listInfo");
			OptionParser base = DSUtil.discord();
			base.accepts("dumpServerInfo", "Collect some minimal info about servers...");
			OptionSet parsed;
			if ((parsed = DSUtil.tryPrintHelp(args, base)) == null) {
				System.exit(1);
				return;
			}
			DiscordApi api = DSUtil.init(parsed);
			Path root = (Path) parsed.valueOf("dir");
			UserGlobalInfo info = new UserGlobalInfo(api, parsed.has("dumpServerInfo"));
			try (BufferedWriter w = IOHelper.newWriter(root.resolve("info.json"))) {
				DSUtil.JSON.toJson(info, UserGlobalInfo.class, w);
			}
			break;
		case "fetchAtt":
			System.out.println("Mode: fetchAtt");
			AttachmentFetcher.main(realArgs);
			break;
		default:
			System.out.println("Unsupported mode!");
			System.exit(1);
			break;
		}
		System.exit(0);
	}

	private HelloClass() {
	}
}
