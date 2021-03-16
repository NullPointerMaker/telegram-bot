package npDev.telegramBot.shell;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import npDev.telegramBot.Instance;
import npDev.telegramBot.Utilities;

import java.util.LinkedList;
import java.util.TimerTask;

public class MessageShell {
    static final MessageShell NULL = new MessageShell(null);
    private final Integer messageID;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isNull, hasText, hasCaption, hasUrl;
    private final Integer forwardFromMessageID;
    private final Integer editDate;
    private final Integer date;
    private final String text;
    private final MessageEntity[] entities;
    private final String forwardSenderName;
    private final String forwardSignature;
    private final ChatShell chat,senderChat,forwardFromChat;
    private final MessageShell replyToMessage,pinnedMessage;
    private final DocumentShell document;
    private final UserShell from,forwardFrom;

    public MessageShell(Message message) {
        isNull = message == null;
        messageID = isNull ? null : message.messageId();
        forwardFromMessageID = isNull ? null : message.forwardFromMessageId();
        date = isNull ? null : message.date();
        editDate = isNull ? null : message.editDate();
        String t = isNull ? null : message.text();
        hasText = t != null && t.length() > 0;
        String ct = isNull ? null : message.caption();
        hasCaption = ct != null && ct.length() > 0;
        text = hasText ? t : hasCaption ? ct : null;
        entities = hasText ? message.entities() : hasCaption ? message.captionEntities() : null;
        hasUrl = hasUrl(entities);
        forwardSenderName = isNull ? null : message.forwardSenderName();
        forwardSignature = isNull ? null : message.forwardSignature();
        chat = isNull ? ChatShell.NULL : new ChatShell(message.chat());
        forwardFromChat = isNull ? ChatShell.NULL : new ChatShell(message.forwardFromChat());
        ChatShell scChat = isNull ? ChatShell.NULL : new ChatShell(message.senderChat());
        senderChat = forwardFromChat.equals(scChat) ? ChatShell.NULL : scChat;//转自当前聊天则清除发送方
        replyToMessage = isNull ? MessageShell.NULL : new MessageShell(message.replyToMessage());
        pinnedMessage = isNull ? MessageShell.NULL : new MessageShell(message.pinnedMessage());
        document = isNull ? DocumentShell.NULL : new DocumentShell(message.document());
        from= isNull ? UserShell.NULL : new UserShell(message.from());
//        from=!fUser.isNull()&&fUser.getID()==777000?UserShell.NULL:fUser;//发送方是系统则清除发送方
        UserShell ffUser = isNull ? UserShell.NULL : new UserShell(message.forwardFrom());
        forwardFrom = !from.isNull()&&from.equals(ffUser) ? UserShell.NULL : ffUser;//发送人和转发人一样只保留发送人
    }

    static MessageShell doSend(Instance bot, long chatId, String text, Integer replyToMessageId, Boolean disableWebPagePreview, Keyboard keyboard) {
        SendMessage request = new SendMessage(chatId, text).parseMode(ParseMode.Markdown);
        if (replyToMessageId != null) {
            request.replyToMessageId(replyToMessageId).allowSendingWithoutReply(false);
        }
        if (disableWebPagePreview != null) {
            request.disableWebPagePreview(disableWebPagePreview);
        }
        if (keyboard != null) {
            request.replyMarkup(keyboard);
        }
        SendResponse sendResponse = bot.execute(request);
        return new MessageShell(sendResponse.message());
    }

    public static String getPrivateLink(String privateId) {
        return privateId.replace("-100", "https://t.me/c/");
    }

    private static String getPrivateID(long chatID, int messageID) {
        return chatID + "/" + messageID;
    }

    private static String getPublicLink(String chatPublicLink, int messageID) {
        return chatPublicLink + "/" + messageID;
    }

    //	public boolean textHasUrl() {
//		return entities!=null&&entitiesHasUrl(entities);
//	}
//	public boolean captionHasUrl() {
//		return captionEntities!=null&&entitiesHasUrl(captionEntities());
//	}
    private static boolean hasUrl(MessageEntity[] entities) {
        if (entities == null) {
            return false;
        }
        for (MessageEntity entity : entities) {
            if (MessageEntity.Type.text_link.equals(entity.type()) || MessageEntity.Type.url.equals(entity.type())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param text     文本
     * @param entities 元素
     * @return 数组0是url用%s占位的完整消息，其余的是消息中包含的url，注意可能包含markdown转义字符`\`
     */
    private static String[] toMarkdownAndUrls(String text, MessageEntity[] entities) {
        LinkedList<String> list = new LinkedList<>();
        StringBuffer sb = new StringBuffer(text);
        int offsetFromOldToNew = 0;
        int startOld, lengthOld, endOld;
        for (MessageEntity entity : entities) {
            startOld = entity.offset() + offsetFromOldToNew;
            lengthOld = entity.length();
            endOld = startOld + lengthOld;
            String s;
            try {
                s = sb.substring(startOld, endOld);
            } catch (Exception e) {
                continue;
            }
            switch (entity.type()) {
                case text_link:
                    s = "\b[" + Utilities.prepareNotToMarkdown(s) + "](\bs)";
                    list.add(entity.url());
                    break;
                case url:
                    if (!s.contains("://")) {//没有协议头
                        s = "http://" + s;
                    }
                    list.add(Utilities.completeToMarkdown(s));
                    s = "\bs";
                    break;
                case text_mention:
                    s = "\b" + UserShell.toMarkdown(s, entity.user().id());
                    break;
                case code:
//                    if (startOld <= 3 && !text.startsWith("转自"))
                    s = "\b`" + Utilities.prepareNotToMarkdown(s) + "\b`";
                    break;
                case pre:
//                    if (startOld <= 3 && !text.startsWith("转自"))
                    s = "\b`\b`\b`" + Utilities.prepareNotToMarkdown(s) + "\b`\b`\b`";
                    break;
                case bold:
//                    s = "\b*" + Utilities.prepareNotToMarkdown(s) + "\b*";
//                    break;
                case italic:
//                    s = "\b_" + Utilities.prepareNotToMarkdown(s) + "\b_";
//                    break;
                default:
                    break;
            }
            sb = sb.replace(startOld, endOld, s);
            offsetFromOldToNew += (s.length() - lengthOld);
        }
        list.addFirst(Utilities.toStringFormat(Utilities.completeToMarkdown(sb.toString())));
        return list.toArray(new String[0]);
    }

    public void doDelete(Instance bot) {
        try {
            DeleteMessage request = new DeleteMessage(chat.getID(), messageID);
            bot.execute(request);
        } catch (Exception e) {
//            e.printStackTrace();
            doDeleteAfter(bot, 3);
        }
    }

    public void doDeleteAfter(final Instance bot, int second) {
        Utilities.TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                doDelete(bot);
            }
        }, second * 1000L);
    }

    public MessageShell doReply(Instance bot, String text, Boolean disableWebPagePreview, Keyboard keyboard) {
        return doSend(bot, chat.getID(), text, messageID, disableWebPagePreview, keyboard);
    }

    public void doPin(Instance bot, Boolean disableNotification) {
        PinChatMessage request = new PinChatMessage(chat.getID(), messageID);
        if (disableNotification != null) {
            request.disableNotification(disableNotification);
        }
        bot.execute(request);
    }

    public void doUnpin(Instance bot) {
        UnpinChatMessage request = new UnpinChatMessage(chat.getID());
        request.messageId(messageID);
        bot.execute(request);
    }

    public void doEditText(Instance bot, String text) {
        EditMessageText request = new EditMessageText(chat.getID(), messageID, text).parseMode(ParseMode.Markdown).disableWebPagePreview(true);
        bot.execute(request);
    }

    public void doEditReplyMarkup(Instance bot, InlineKeyboardMarkup markup) {
        EditMessageReplyMarkup request = new EditMessageReplyMarkup(chat.getID(), messageID).replyMarkup(markup);
        bot.execute(request);
    }

    @SuppressWarnings("UnusedReturnValue")
    public MessageShell doRepost(Instance bot, String text) {
        return doSend(bot, chat.getID(), text, replyToMessage.messageID, null, null);
    }

    public Integer getLastDate() {
        return editDate == null ? date : editDate;
    }

    public boolean hasCommand(String command, String atUsername) {
        return text != null && text.matches("(?i)[/!！]" + command + "(" + atUsername + ")?\\b(.*)");
    }

    public boolean hasDocument() {
        return !document.isNull();
    }

    public MessageShell getReplyToMessage() {
        return replyToMessage;
    }

    public UserShell getFrom() {
        return from;
    }

    public ChatMemberShell doGetChatMember(Instance bot) {
        return ChatMemberShell.doGetChatMember(bot, chat.getID(), from.getID());
    }

    public ChatShell getChat() {
        return chat;
    }

    public boolean hasText() {
        return hasText;
    }
//    private boolean hasCaption() {
//        return hasCaption;
//    }

    public String getPrivateID() {
        return getPrivateID(chat.getID(), messageID);
    }

    private String getForwardFromMessagePrivateID() {
        if (forwardFromChat.isNull()) {//没有转发来源
            return null;
        }
        if (forwardFromMessageID == null) {//没有转发来源
            return null;
        }
        return getPrivateID(forwardFromChat.getID(), forwardFromMessageID);
    }

    public String getForwardFromMarkdown() {
        String str = getForwardFromMessageMarkdown();
        if (str != null) {//从频道转发
            return str;
        }
        if (!forwardFrom.isNull()) {//从用户转发
            return forwardFrom.toMentionMarkdown();
        }
        if (forwardSenderName != null) {//用户设置隐私
            return "`" + forwardSenderName + "`";
        }
        return null;
    }

    public String getFromMarkdown() {
        if (!senderChat.isNull()) {//来自聊天
            String link = senderChat.getPublicLink();
            if (link == null) {
                return "`" + senderChat.getTitle() + "`";
            }
            return "[" + senderChat.getTitle() + "](" + link + ")";
        }
        if (!from.isNull()) {//来自用户
            return from.toMentionMarkdown();
        }
        return null;
    }

    private String getForwardFromMessageMarkdown() {
        if (forwardFromChat.isNull()) {//转自频道或匿名
            return null;
        }
        String link = getForwardFromMessageLink();
        StringBuilder sb = new StringBuilder();
        if (link != null) {//有来源链接
            sb.append("[").append(forwardFromChat.getTitle());
            if (forwardSignature != null) {//有签名
                sb.append("（").append(forwardSignature).append("）");
            }
            sb.append("](").append(link).append(")");
        } else {
            sb.append("`").append(forwardFromChat.getTitle()).append("`");
        }
        return sb.toString();
    }

    private String getForwardFromMessageLink() {
        if (forwardFromChat.isNull()) {//没有转发来源
            return null;
        }
        if (forwardFromMessageID == null) {//没有转发来源
            return null;
        }
        String forwardFromMessagePrivateID = getForwardFromMessagePrivateID();
        if (forwardFromMessagePrivateID == null) {
            return null;
        }
        String cpl = forwardFromChat.getPublicLink();
        return cpl == null ? getPrivateLink(getForwardFromMessagePrivateID()) : getPublicLink(cpl, forwardFromMessageID);
    }
    public String getLinkOrPrivateID(){
        String cpl=chat.getPublicLink();
        return cpl==null?getPrivateLink(getPrivateID()):getPublicLink(cpl,messageID);
    }


    public boolean hasUrl() {
        return hasUrl;
    }

    public boolean isNull() {
        return isNull;
    }

    public UserShell getForwardFrom() {
        return forwardFrom;
    }

    public Integer getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public String[] toMarkdownAndUrls() {
        return hasUrl ? toMarkdownAndUrls(text, entities) : null;
    }
    public MessageShell getPinnedMessage(){
        return pinnedMessage;
    }
}