package ru.zaxar163.dsdmp.entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.Webhook;

public class UserCache {
	public Map<Long, String> users = new ConcurrentHashMap<>();
	public Map<Long, String> webhooks = new ConcurrentHashMap<>();
	public final transient Consumer<User> userConsumer;
	public final transient Consumer<Webhook> webhookConsumer;
	public final transient Consumer<MessageAuthor> messageAuthorConsumer;

	public UserCache() {
		userConsumer = e -> users.put(e.getId(), e.getDiscriminatedName());
		webhookConsumer = e -> webhooks.put(e.getId(), e.getName().orElse("name of webhook not present"));
		messageAuthorConsumer = e -> {
			if (e.isWebhook())
				webhooks.put(e.getId(), e.getDiscriminatedName());
			else if (e.isUser())
				users.put(e.getId(), e.asUser().get().getDiscriminatedName());
		};
	}
}
