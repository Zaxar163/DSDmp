package ru.zaxar163.dsdmp.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageSet;

public class DumpedTextChannel extends CommonInfo {
	public final transient TextChannel ch;
	public List<TextMessage> messages;
	public transient UserCache realUsers;

	public DumpedTextChannel() {
		ch = null;
		realUsers = null;
		messages = new ArrayList<>();
	}

	public DumpedTextChannel(final TextChannel ch) {
		id = ch.getId();
		date = ch.getCreationTimestamp().toEpochMilli();
		this.ch = ch;
		realUsers = users = new UserCache();
		messages = new ArrayList<>();
	}

	public DumpedTextChannel(final TextChannel ch, UserCache users) {
		id = ch.getId();
		date = ch.getCreationTimestamp().toEpochMilli();
		this.ch = ch;
		this.users = null;
		realUsers = users;
		messages = new ArrayList<>();
	}

	public void dump(int messageRate, long delayMillis) throws InterruptedException {
		if (!ch.canReadMessageHistory(ch.getApi().getYourself()))
			return;
		MessageSet m = ch.getMessages(messageRate).join();
		Consumer<MessageAuthor> authorAcceptor = users.messageAuthorConsumer;
		while (!m.isEmpty()) {
			((ArrayList<TextMessage>) messages).ensureCapacity(messages.size() + m.size() + 4);
			Iterator<Message> msg = m.descendingIterator();
			while (msg.hasNext())
				messages.add(new TextMessage(msg.next(), authorAcceptor));
			m = ch.getMessagesBefore(messageRate, m.getOldestMessage().get()).join();
			if (delayMillis != 0) Thread.sleep(delayMillis);
		}
		// messages.sort(java.util.Comparator.comparingDouble(e -> e.date)); not needed
		((ArrayList<TextMessage>) messages).trimToSize();
	}

	public void dumpNew(int messageRate, long delayMillis) {
		// TODO support ratelimited dumping continue!!! ADD LOG4J-core!!!
		throw new IllegalArgumentException("unimplemented");
	}
}
