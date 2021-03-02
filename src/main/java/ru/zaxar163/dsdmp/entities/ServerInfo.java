package ru.zaxar163.dsdmp.entities;

import java.util.List;
import java.util.stream.Collectors;

import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;

public class ServerInfo extends CommonInfo {
	public static class RoleSerialized {
		public List<Long> users;
		public String name;
		public long id;
		public int position, allowed, denied;

		public RoleSerialized() {
		}

		public RoleSerialized(Role r) {
			name = r.getName();
			id = r.getId();
			position = r.getRawPosition();
			Permissions perms = r.getPermissions();
			allowed = perms.getAllowedBitmask();
			denied = perms.getDeniedBitmask();
			users = r.getUsers().stream().map(e -> e.getId()).collect(Collectors.toList());
		}
	}

	public long owner;
	public List<RoleSerialized> roles;

	public String name;

	public ServerInfo() {
	}

	public ServerInfo(Server srv) {
		id = srv.getId();
		name = srv.getName();
		date = srv.getCreationTimestamp().toEpochMilli();
		owner = srv.getOwnerId();
		users = new UserCache();
		srv.getOwner().ifPresent(users.userConsumer);
		srv.getMembers().stream().filter(e -> e != null).forEach(users.userConsumer);
		roles = srv.getRoles().stream().map(RoleSerialized::new).collect(Collectors.toList());
	}
}
