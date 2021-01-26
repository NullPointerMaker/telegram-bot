package npDev.telegramBot.shell;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.ChatPermissions;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.RestrictChatMember;
import com.pengrad.telegrambot.response.GetChatMemberResponse;
import npDev.telegramBot.Instance;

public class ChatMemberShell {
    private final Long chatID;
    private final UserShell user;
    //    private final ChatMember chatMember;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isNull;
    private final int untilDate;
    private final ChatMember.Status status;
    private final Boolean canSendMessages;

    protected ChatMemberShell(final ChatMember chatMember, final Long chatID) {
        isNull = chatMember == null;
        this.chatID = chatID;
        final Integer ud = isNull ? null : chatMember.untilDate();
        untilDate = ud != null && ud > 0 ? ud : Integer.MAX_VALUE;
        status = isNull ? null : chatMember.status();
        canSendMessages = !ChatMember.Status.kicked.equals(status) && (!ChatMember.Status.restricted.equals(status) || chatMember.canSendMessages());
        user = isNull ? UserShell.NULL : new UserShell(chatMember.user());
    }

    static ChatMemberShell doGetChatMember(Instance bot, long chatID, int userID) {
        GetChatMember request = new GetChatMember(chatID, userID);
        GetChatMemberResponse response = bot.execute(request);
        if (response.isOk()) {
            return new ChatMemberShell(response.chatMember(), chatID);
        } else {
            //throw new NullPointerException("%s is null");
            return new ChatMemberShell(null, null);
        }
    }

    /**
     * 禁言一段时间。如果现在禁言中且比本次时间长，则不执行缩短期限。
     *
     * @param seconds 禁言秒数
     * @return 是否执行
     */
    public boolean doMuteOrMore(Instance bot, int seconds) {
        int ud = (int) (System.currentTimeMillis() / 1000 + seconds);
        if (!canSendMessages && ud < untilDate) {//已被禁言且比已有刑期短
            return false;//不执行
        }
        ChatPermissions permissions = new ChatPermissions().canSendMessages(false).canSendMediaMessages(false).canSendOtherMessages(false).canSendPolls(false);
        RestrictChatMember request = new RestrictChatMember(chatID, user.getID(), permissions).untilDate(ud);
        bot.execute(request);
        return true;//如果禁言失败则抛出异常而不是返回
    }

    private boolean isCreator() {
        return ChatMember.Status.creator.equals(status);
    }

    private boolean isAdministrator() {
        return ChatMember.Status.administrator.equals(status);
    }

    public boolean isModerator() {
        return isCreator() || isAdministrator();
    }

    public Boolean canSendMessages() {
        return canSendMessages;
    }
    public Long getChatID(){
        return chatID;
    }
    public UserShell getUser(){
        return user;
    }
}
