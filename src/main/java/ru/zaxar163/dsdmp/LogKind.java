package ru.zaxar163.dsdmp;

public enum LogKind {
	DUMP_START("Dumping: "), MESSAGES("Dumped messages count: "), DUMP_END("Dumped: ");
	public final String append;

	LogKind(String append) {
		this.append = append;
	}
}
