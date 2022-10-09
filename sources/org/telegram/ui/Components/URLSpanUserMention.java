package org.telegram.ui.Components;

import android.text.TextPaint;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TextStyleSpan;
/* loaded from: classes3.dex */
public class URLSpanUserMention extends URLSpanNoUnderline {
    private int currentType;
    private TextStyleSpan.TextStyleRun style;

    public URLSpanUserMention(String str, int i) {
        this(str, i, null);
    }

    public URLSpanUserMention(String str, int i, TextStyleSpan.TextStyleRun textStyleRun) {
        super(str);
        this.currentType = i;
        this.style = textStyleRun;
    }

    @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        int i = this.currentType;
        if (i == 3) {
            textPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        } else if (i == 2) {
            textPaint.setColor(-1);
        } else if (i == 1) {
            textPaint.setColor(Theme.getColor("chat_messageLinkOut"));
        } else {
            textPaint.setColor(Theme.getColor("chat_messageLinkIn"));
        }
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(textPaint);
        } else {
            textPaint.setUnderlineText(false);
        }
    }
}
