package com.jensen.draculadaybyday.entries;

public enum EntryType {
    DIARY_ENTRY("Diary Entry"),
    LETTER("Letter"),
    TELEGRAM("Telegram"),
    PHONOGRAPH("Phonograph"),
    NEWSPAPER("Newspaper"),
    NOTE("Note");

    public final String description;

    EntryType(String description) {
        this.description = description;
    }
}
