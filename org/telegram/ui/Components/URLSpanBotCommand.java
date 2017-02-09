package org.telegram.ui.Components;

import android.text.TextPaint;
import org.telegram.ui.ActionBar.Theme;

public class URLSpanBotCommand extends URLSpanNoUnderline {
    public static boolean enabled = true;

    public URLSpanBotCommand(String url) {
        super(url);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Theme.getColor(enabled ? Theme.key_chat_messageLink : Theme.key_chat_messageText));
        ds.setUnderlineText(false);
    }
}
