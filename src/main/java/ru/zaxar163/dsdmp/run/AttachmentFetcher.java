package ru.zaxar163.dsdmp.run;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarHeader;
import org.kamranzafar.jtar.TarOutputStream;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import ru.zaxar163.dsdmp.entities.Attachment;
import ru.zaxar163.dsdmp.entities.DumpedTextChannel;
import ru.zaxar163.dsdmp.entities.TextMessage;
import ru.zaxar163.dsdmp.util.DSUtil;
import ru.zaxar163.dsdmp.util.IOHelper;

// written for DumpUser.dump or DumpServer.dump, DumpGroup.dump :D
public final class AttachmentFetcher {
	public static void main(final String[] args) throws IOException {
		OptionParser base = DSUtil.base();
		OptionSet parsed;
		if ((parsed = DSUtil.tryPrintHelp(args, base)) == null) {
			System.exit(1);
			return;
		}
		Path root = (Path) parsed.valueOf("dir");
		Path channels = root.resolve("channels");
		final boolean notOverwrite = !parsed.has("overwrite");
		final AtomicInteger cnt = new AtomicInteger(0);
		Files.list(channels).filter(IOHelper::isDir).forEach(path -> {
			Path archG = path.resolve("attachments.tar.gz");
			Path arch = path.resolve("attachments.tar.gz.tmp");
			final String channelId = channels.relativize(path).toString();
			System.out.println("Downloading for: " + channelId + ", number: " + cnt.incrementAndGet() + ".");
			if (IOHelper.exists(archG) && notOverwrite) {
				System.out.println("Already downloaded for this channel!");
				return;
			}
			try {
				Files.deleteIfExists(arch);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			try (TarOutputStream out = new TarOutputStream(new GZIPOutputStream(IOHelper.newOutput(arch)))) {
				DumpedTextChannel d;
				try (Reader r = IOHelper.newReader(path.resolve("ch.json"))) {
					d = DSUtil.JSON.fromJson(r, DumpedTextChannel.class);
				}
				for (TextMessage m : d.messages)
					if (m.attachments != null)
						for (final Attachment at : m.attachments) {
							System.out.println("Downloading " + at.id + "...");
							final TarEntry ent = new TarEntry(
									TarHeader.createHeader(String.valueOf(at.id), at.size, 0, false, 0444));
							ent.setGroupId(0);
							ent.setUserId(0);
							ent.setUserName("root");
							out.putNextEntry(ent);
							try (InputStream inat = IOHelper.newInput(IOHelper.convertToURL(at.url))) {
								IOHelper.transfer(inat, out);
							}
						}
			} catch (final Throwable exc) {
				throw new RuntimeException(exc);
			}
			try {
				IOHelper.move(arch, archG);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private AttachmentFetcher() {
	}

}
