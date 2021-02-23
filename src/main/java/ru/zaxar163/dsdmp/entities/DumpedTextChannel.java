package ru.zaxar163.dsdmp.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageSet;

public class DumpedTextChannel {
	public final transient TextChannel ch;
	public long id;
	public UserCache users;
	public List<TextMessage> messages;
	public transient UserCache realUsers;

	public DumpedTextChannel() {
		ch = null;
		realUsers = null;
	}

	public DumpedTextChannel(final TextChannel ch) {
		id = ch.getId();
		this.ch = ch;
		realUsers = users = new UserCache();
		messages = new ArrayList<>();
	}

	public DumpedTextChannel(final TextChannel ch, UserCache users) {
		id = ch.getId();
		this.ch = ch;
		this.users = null;
		this.realUsers = users;
		messages = new ArrayList<>();
	}

	public void dump() throws InterruptedException {
		MessageSet m = ch.getMessages(1000).join();
		Consumer<MessageAuthor> authorAcceptor = users.messageAuthorConsumer;
		while (!m.isEmpty()) {
			((ArrayList<TextMessage>) messages).ensureCapacity(messages.size() + m.size() + 4);
			Iterator<Message> msg = m.descendingIterator();
			while (msg.hasNext())
				messages.add(new TextMessage(msg.next(), authorAcceptor));
			m = ch.getMessagesBefore(1000, m.getOldestMessage().get()).join();
			Thread.sleep(100);
		}
		// messages.sort(java.util.Comparator.comparingDouble(e -> e.date)); not needed
		((ArrayList<TextMessage>) messages).trimToSize();
	}

	public void dumpNew() {
		// TODO support ratelimited dumping continue!!!
		throw new IllegalArgumentException("unimplemented");
	}
}
