package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan.TextStyleRun;

public class URLSpanReplacement extends URLSpan {
    private TextStyleRun style;

    public URLSpanReplacement(String str) {
        this(str, null);
    }

    public URLSpanReplacement(String str, TextStyleRun textStyleRun) {
        super(str);
        this.style = textStyleRun;
    }

    public TextStyleRun getTextStyleRun() {
        return this.style;
    }

    public void onClick(View view) {
        Browser.openUrl(view.getContext(), Uri.parse(getURL()));
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(textPaint);
        }
    }
}
