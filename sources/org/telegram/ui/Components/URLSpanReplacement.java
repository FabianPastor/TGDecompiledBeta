package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan;

public class URLSpanReplacement extends URLSpan {
    private TextStyleSpan.TextStyleRun style;

    public URLSpanReplacement(String str) {
        this(str, (TextStyleSpan.TextStyleRun) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public URLSpanReplacement(String str, TextStyleSpan.TextStyleRun textStyleRun) {
        super(str != null ? str.replace(8238, ' ') : str);
        this.style = textStyleRun;
    }

    public TextStyleSpan.TextStyleRun getTextStyleRun() {
        return this.style;
    }

    public void onClick(View view) {
        Browser.openUrl(view.getContext(), Uri.parse(getURL()));
    }

    public void updateDrawState(TextPaint textPaint) {
        int color = textPaint.getColor();
        super.updateDrawState(textPaint);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(textPaint);
            textPaint.setUnderlineText(textPaint.linkColor == color);
        }
    }
}
