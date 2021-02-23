package ru.zaxar163.dsdmp.entities;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;

public class TextMessage {
	public List<Attachment> attachments;
	public Map<String, Integer> reactions;
	public long authorId;
	public String content;
	public long date;
	public long modified; // -1 if is not modified
	public long id;
	public boolean tts;
	public boolean pinned;

	public TextMessage() {
	}

	public TextMessage(final Message e, Consumer<MessageAuthor> cacher) {
		content = e.getContent();
		date = e.getCreationTimestamp().toEpochMilli();
		Instant modified = e.getLastEditTimestamp().orElse(null);
		this.modified = modified != null ? modified.toEpochMilli() : -1;
		authorId = e.getAuthor().getId();
		cacher.accept(e.getAuthor());
		tts = e.isTts();
		pinned = e.isPinned();
		reactions = e.getReactions().stream()
				.collect(Collectors.toMap(f -> f.getEmoji().getMentionTag(), f -> f.getCount()));
		if (e.getAttachments() != null)
			attachments = e.getAttachments().stream().map(t -> new Attachment(t)).collect(Collectors.toList());
		id = e.getId();
	}

}
