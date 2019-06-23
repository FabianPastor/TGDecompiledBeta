package org.telegram.ui.Components;

import android.text.TextPaint;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TextStyleSpan.TextStyleRun;

public class URLSpanUserMention extends URLSpanNoUnderline {
    private int currentType;
    private TextStyleRun style;

    public URLSpanUserMention(String str, int i) {
        this(str, i, null);
    }

    public URLSpanUserMention(String str, int i, TextStyleRun textStyleRun) {
        super(str);
        this.currentType = i;
        this.style = textStyleRun;
    }

    public void onClick(View view) {
        super.onClick(view);
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        int i = this.currentType;
        if (i == 2) {
            textPaint.setColor(-1);
        } else if (i == 1) {
            textPaint.setColor(Theme.getColor("chat_messageLinkOut"));
        } else {
            textPaint.setColor(Theme.getColor("chat_messageLinkIn"));
        }
        TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(textPaint);
        } else {
            textPaint.setUnderlineText(false);
        }
    }
}
