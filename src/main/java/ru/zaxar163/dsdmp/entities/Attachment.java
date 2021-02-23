package ru.zaxar163.dsdmp.entities;

import org.javacord.api.entity.message.MessageAttachment;

public class Attachment {
	public long date;
	public long id;
	public boolean isImage;
	public boolean isSpoiler;
	public String name;
	public int size;
	public String url;

	public Attachment() {
	}

	public Attachment(final MessageAttachment t) {
		try {
			url = t.getUrl().toURI().toASCIIString();
		} catch (final Throwable e) {
			url = "no";
		}
		name = t.getFileName() == null ? "empty" : t.getFileName();
		size = t.getSize();
		isSpoiler = t.isSpoiler();
		isImage = t.isImage();
		id = t.getId();
		date = t.getCreationTimestamp().toEpochMilli();
	}
}
