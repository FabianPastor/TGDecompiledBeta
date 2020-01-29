package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan;

public class URLSpanNoUnderline extends URLSpan {
    private TextStyleSpan.TextStyleRun style;

    public URLSpanNoUnderline(String str) {
        this(str, (TextStyleSpan.TextStyleRun) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public URLSpanNoUnderline(String str, TextStyleSpan.TextStyleRun textStyleRun) {
        super(str != null ? str.replace(8238, ' ') : str);
        this.style = textStyleRun;
    }

    public void onClick(View view) {
        String url = getURL();
        if (url.startsWith("@")) {
            Browser.openUrl(view.getContext(), Uri.parse("https://t.me/" + url.substring(1)));
            return;
        }
        Browser.openUrl(view.getContext(), url);
    }

    public void updateDrawState(TextPaint textPaint) {
        int i = textPaint.linkColor;
        int color = textPaint.getColor();
        super.updateDrawState(textPaint);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(textPaint);
        }
        textPaint.setUnderlineText(i == color);
    }
}
