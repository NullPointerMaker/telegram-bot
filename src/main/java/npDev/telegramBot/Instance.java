package npDev.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.BaseResponse;
import npDev.telegramBot.shell.UserShell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public abstract class Instance implements UpdatesListener {
    private final Properties conf = new Properties();
    private final String fileName;
    private UserShell user;
    private TelegramBot bot;

    public Instance(String fileName) {
        this.fileName = fileName;
        load();
    }

    private void load() {
        if (bot != null) {
            bot.removeGetUpdatesListener();
        }
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
            conf.clear();
            conf.load(new BufferedReader(new FileReader(file)));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        bot = new TelegramBot(getAPIToken());
        user = new UserShell(execute(new GetMe()).user());
        bot.setUpdatesListener(this);
    }

    @SuppressWarnings("unused")
    protected Boolean getBoolean(String key, Boolean def) {
        try {
            switch (conf.getProperty(key)) {
                case "true":
                case "t":
                case "yes":
                case "y":
                    return true;
                case "flase":
                case "f":
                case "no":
                case "n":
                    return false;
                default:
                    return def;
            }
        } catch (Exception e) {
            return def;
        }
    }

    protected Integer getInteger(String key, Integer def) {
        Integer v = def;
        try {
            v = Integer.valueOf(conf.getProperty(key));
        } catch (NumberFormatException e) {
            // e.printStackTrace();
        }
        return v;
    }

    private String getAPIToken() {
        return conf.getProperty("api_token");
    }

    public UserShell getUser() {
        return user;
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
        return bot.execute(request);
    }

    public String getStartLink(String parameter){
        return user.getPublicLink()+"?start="+parameter;
    }
}