package org.telegram.ui.Components;

import android.text.TextPaint;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TextStyleSpan;

public class URLSpanUserMention extends URLSpanNoUnderline {
    private int currentType;
    private TextStyleSpan.TextStyleRun style;

    public URLSpanUserMention(String url, int type) {
        this(url, type, (TextStyleSpan.TextStyleRun) null);
    }

    public URLSpanUserMention(String url, int type, TextStyleSpan.TextStyleRun run) {
        super(url);
        this.currentType = type;
        this.style = run;
    }

    public void onClick(View widget) {
        super.onClick(widget);
    }

    public void updateDrawState(TextPaint p) {
        super.updateDrawState(p);
        int i = this.currentType;
        if (i == 3) {
            p.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        } else if (i == 2) {
            p.setColor(-1);
        } else if (i == 1) {
            p.setColor(Theme.getColor("chat_messageLinkOut"));
        } else {
            p.setColor(Theme.getColor("chat_messageLinkIn"));
        }
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
        } else {
            p.setUnderlineText(false);
        }
    }
}
