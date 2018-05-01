package org.telegram.ui.Components;

import android.text.TextPaint;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public class URLSpanUserMention extends URLSpanNoUnderline {
    private int currentType;

    public URLSpanUserMention(String str, int i) {
        super(str);
        this.currentType = i;
    }

    public void onClick(View view) {
        super.onClick(view);
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        if (this.currentType == 2) {
            textPaint.setColor(-1);
        } else if (this.currentType == 1) {
            textPaint.setColor(Theme.getColor(Theme.key_chat_messageLinkOut));
        } else {
            textPaint.setColor(Theme.getColor(Theme.key_chat_messageLinkIn));
        }
        textPaint.setUnderlineText(false);
    }
}
