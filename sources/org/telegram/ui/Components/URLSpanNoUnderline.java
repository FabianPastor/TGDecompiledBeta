package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan.TextStyleRun;

public class URLSpanNoUnderline extends URLSpan {
    private TextStyleRun style;

    public URLSpanNoUnderline(String str) {
        this(str, null);
    }

    public URLSpanNoUnderline(String str, TextStyleRun textStyleRun) {
        super(str);
        this.style = textStyleRun;
    }

    public void onClick(View view) {
        String url = getURL();
        if (url.startsWith("@")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://t.me/");
            stringBuilder.append(url.substring(1));
            Browser.openUrl(view.getContext(), Uri.parse(stringBuilder.toString()));
            return;
        }
        Browser.openUrl(view.getContext(), url);
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(textPaint);
        } else {
            textPaint.setUnderlineText(false);
        }
    }
}
