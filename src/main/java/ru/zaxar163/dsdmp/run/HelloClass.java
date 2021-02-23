package ru.zaxar163.dsdmp.run;

import java.io.IOException;
import java.util.Arrays;

public class HelloClass {
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Modes: dumpPrivate, dumpServer, fetchAtt");
			System.exit(1);
		}
		String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
		switch (args[0]) {
		case "dumpPrivate":
			DSPrivateDumper.main(realArgs);
			break;
		case "dumpServer":
			DSServerDumper.main(realArgs);
			break;
		case "fetchAtt":
			AttachmentFetcher.main(realArgs);
			break;
		default:
			System.out.println("Unsupported mode!");
			System.exit(1);
			break;
		}
		System.exit(0);
	}
}
