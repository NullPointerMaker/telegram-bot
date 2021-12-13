package npDev.telegramBot.shell;

import com.pengrad.telegrambot.model.User;
import npDev.telegramBot.Instance;

public class UserShell {
    static final UserShell NULL = new UserShell(null);
    private final boolean isNull;
    private final Long id;
    private final String firstName;
    private final String username;
    private final Boolean isBot;

    public UserShell(User user) {
        isNull = user == null;
        id = isNull ? null : user.id();
        firstName = isNull ? null : user.firstName();
        username = isNull ? null : user.username();
        isBot = isNull ? null : user.isBot();
    }

    static String toMarkdown(String userName, long userID) {
        return "[" + userName + "](tg://user?id=" + userID + ")";
    }

    public static String toIDMarkdown(long userID) {
        return toMarkdown(String.valueOf(userID), userID);
    }
//    public String toIDMarkdown() {
//        return id==null ? null : toMarkdown(String.valueOf(id),id);
//    }

    public String getAtUsername() {
        return username == null ? null : "@" + username;
    }

    public String getPublicLink() {
        return username == null ? null : "https://t.me/" + username;
    }

    @SuppressWarnings("UnusedReturnValue")
    public MessageShell doSendMessage(Instance bot, String text, Boolean disableWebPagePreview) {
        return MessageShell.doSend(bot, id, text, null, disableWebPagePreview, null);
    }

    public ChatMemberShell doGetChatMember(Instance bot, long chatId) {
        return ChatMemberShell.doGetChatMember(bot, chatId, id);
    }

    public boolean equals(UserShell shell) {
        if (isNull && shell.isNull) {
            return true;
        }
        if (isNull || shell.isNull) {
            return false;
        }
        return id.equals(shell.id);
    }

    String toMentionMarkdown() {
        return isNull ? null : toMarkdown(firstName, id);
    }

    public Long getID() {
        return id;
    }

    public boolean isNull() {
        return isNull;
    }

    public boolean isBot() {
        return isBot;
    }
}