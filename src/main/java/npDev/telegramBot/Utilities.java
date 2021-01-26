package npDev.telegramBot;

import java.util.Timer;

public class Utilities {
    public static final Timer TIMER = new Timer();
    /**
     * 如果字符串中有经过<code>prepareNotToStringFormat()</code>处理的转义字符，那么它们将会被转回原始字符。
     * @param text 需要转义成<code>string.format()</code>友好的字符串
     * @return 转义成<code>string.format()</code>友好的字符串
     */
    public static String toStringFormat(String text) {
        text = text.replace("%", "%%");
        text = text.replace("\bs", "%s");
        return text;
    }

    /**
     * 给一段字符串中的<code>string.format()</code>转义字符添加<code>\b</code>标记
     * @param text 需要标记为不转义成<code>string.format()</code>的字符串
     * @return 将<code>string.format()</code>转义字符标记为不转义的字符串
     */
    public static String prepareNotToStringFormat(String text) {
        text = text.replace("%", "\b%");
        return text;
    }

    /**
     * 如果字符串中有经过<code>prepareNotToMarkdown</code>处理的转义字符，那么它们将会被转回原始字符。
     * @param text 需要转义成markdown友好的字符串
     * @return 转义成markdown友好的字符串
     */
    public static String completeToMarkdown(String text) {
        text = text.replace("_", "\\_").replace("*", "\\*").replace("`", "\\`").replace("[", "\\[");
        text = text.replace("\b\\_", "_").replace("\b\\*", "*").replace("\b\\`", "`").replace("\b\\[", "[");
        text= text.replace("\bs","%s");
        return text;
    }

    /**
     * 给一段字符串中的markdown转义字符添加<code>\b</code>标记
     * @param text 需要标记为不转义成markdown的字符串
     * @return 将markdown转义字符标记为不转义的字符串
     */
    public static String prepareNotToMarkdown(String text) {
        text = text.replace("_", "\b_").replace("*", "\b*").replace("`", "\b`").replace("[", "\b[");
        return text;
    }
}
