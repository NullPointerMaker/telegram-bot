package npDev.telegramBot.shell;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.SendChatAction;
import npDev.telegramBot.Instance;

public class ChatShell {
    static final ChatShell NULL = new ChatShell(null);
    private final boolean isNull;
    private final Long id;
    private final Chat.Type type;
    private final String username;
    private final String title;

    public ChatShell(final Chat chat) {
        isNull = chat == null;
        id = isNull ? null : chat.id();
        username = isNull ? null : chat.username();
        title = isNull ? null : chat.title();
        type = isNull ? null : chat.type();
    }

    public boolean isPrivate() {
        return Chat.Type.Private.equals(type);
    }

    private boolean isSupergroup() {
        return Chat.Type.supergroup.equals(type);
    }

    private boolean isGroup() {
        return Chat.Type.group.equals(type);
    }

    public boolean isGroupChat() {
        return isGroup() || isSupergroup();
    }

    public ChatMemberShell doGetChatMember(Instance bot, long userId) {
        return ChatMemberShell.doGetChatMember(bot, id, userId);
    }

    //	MessageShell doSendMessage(String text,Keyboard markup) {
//		return MessageShell.doSend(bot, id, text, replyToMessageId, markup);
//	}
    boolean equals(ChatShell shell) {
        if (isNull && shell.isNull) {
            return true;
        }
        if (isNull || shell.isNull) {
            return false;
        }
        return id.equals(shell.id);
    }

    String getPublicLink() {
        return username == null ? null : "https://t.me/" + username;
    }

    //	private String getPrivateLink() {
//		return String.valueOf(id).replace("-100", "https://t.me/c/");
//	}
//	String getLink() {
//		String link=getPublicLink();
//		if(link==null){
//			link=getPrivateLink();
//		}
//		return link;
//	}
    public void doTyping(Instance bot) {
        SendChatAction request = new SendChatAction(id, ChatAction.typing);
        bot.execute(request);
    }

    public Long getID() {
        return id;
    }

    boolean isNull() {
        return isNull;
    }

    String getTitle() {
        return title;
    }
}
