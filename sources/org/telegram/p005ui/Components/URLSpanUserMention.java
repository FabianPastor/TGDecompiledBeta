package org.telegram.p005ui.Components;

import android.text.TextPaint;
import android.view.View;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Components.URLSpanUserMention */
public class URLSpanUserMention extends URLSpanNoUnderline {
    private int currentType;

    public URLSpanUserMention(String url, int type) {
        super(url);
        this.currentType = type;
    }

    public void onClick(View widget) {
        super.onClick(widget);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        if (this.currentType == 2) {
            ds.setColor(-1);
        } else if (this.currentType == 1) {
            ds.setColor(Theme.getColor(Theme.key_chat_messageLinkOut));
        } else {
            ds.setColor(Theme.getColor(Theme.key_chat_messageLinkIn));
        }
        ds.setUnderlineText(false);
    }
}
