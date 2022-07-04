package org.telegram.ui.Components;

import android.text.TextPaint;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TextStyleSpan;

public class URLSpanBotCommand extends URLSpanNoUnderline {
    public static boolean enabled = true;
    public int currentType;
    private TextStyleSpan.TextStyleRun style;

    public URLSpanBotCommand(String url, int type) {
        this(url, type, (TextStyleSpan.TextStyleRun) null);
    }

    public URLSpanBotCommand(String url, int type, TextStyleSpan.TextStyleRun run) {
        super(url);
        this.currentType = type;
        this.style = run;
    }

    public void updateDrawState(TextPaint p) {
        super.updateDrawState(p);
        int i = this.currentType;
        if (i == 2) {
            p.setColor(-1);
        } else if (i == 1) {
            p.setColor(Theme.getColor(enabled ? "chat_messageLinkOut" : "chat_messageTextOut"));
        } else {
            p.setColor(Theme.getColor(enabled ? "chat_messageLinkIn" : "chat_messageTextIn"));
        }
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
        } else {
            p.setUnderlineText(false);
        }
    }
}
