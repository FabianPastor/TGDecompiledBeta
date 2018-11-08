package org.telegram.p005ui.Components;

import android.text.TextPaint;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Components.URLSpanBotCommand */
public class URLSpanBotCommand extends URLSpanNoUnderline {
    public static boolean enabled = true;
    public int currentType;

    public URLSpanBotCommand(String url, int type) {
        super(url);
        this.currentType = type;
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        if (this.currentType == 2) {
            ds.setColor(-1);
        } else if (this.currentType == 1) {
            ds.setColor(Theme.getColor(enabled ? Theme.key_chat_messageLinkOut : Theme.key_chat_messageTextOut));
        } else {
            ds.setColor(Theme.getColor(enabled ? Theme.key_chat_messageLinkIn : Theme.key_chat_messageTextIn));
        }
        ds.setUnderlineText(false);
    }
}
