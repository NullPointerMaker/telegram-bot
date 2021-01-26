package npDev.telegramBot.shell;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import npDev.telegramBot.Instance;

public class CallbackQueryShell {
    private final MessageShell message;
    private final UserShell from;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isNull;
    private final String id;
    private final String data;

    public CallbackQueryShell(final CallbackQuery callbackQuery) {
        isNull = callbackQuery == null;
        id = isNull ? null : callbackQuery.id();
        data = isNull ? null : callbackQuery.data();
        message = isNull ? MessageShell.NULL : new MessageShell(callbackQuery.message());
        from = isNull ? UserShell.NULL : new UserShell(callbackQuery.from());
    }

    public void doAnswer(Instance bot, String text, boolean showAlert) {
        AnswerCallbackQuery request = new AnswerCallbackQuery(id);
        if (text != null && !"".equals(text)) {
            request.text(text);
        }
        request.showAlert(showAlert);
        bot.execute(request);
    }

    public String log() {
        return message.getPrivateID() + " @" + from.getID() + " " + data;
    }

    public boolean hasData() {
        return data != null && data.length() > 0;
    }

    public MessageShell getMessage() {
        return message;
    }

    public UserShell getFrom() {
        return from;
    }

    public String getData() {
        return data;
    }
}
