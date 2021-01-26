package npDev.telegramBot.shell;

import com.pengrad.telegrambot.model.Document;

public class DocumentShell {
    static final DocumentShell NULL = new DocumentShell(null);
    private final boolean isNull;

    public DocumentShell(Document document) {
        isNull = document == null;
    }

    public boolean isNull() {
        return isNull;
    }
}