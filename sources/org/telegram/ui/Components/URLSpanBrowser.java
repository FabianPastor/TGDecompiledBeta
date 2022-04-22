package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan;

public class URLSpanBrowser extends URLSpan {
    private TextStyleSpan.TextStyleRun style;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public URLSpanBrowser(String str, TextStyleSpan.TextStyleRun textStyleRun) {
        super(str != null ? str.replace(8238, ' ') : str);
        this.style = textStyleRun;
    }

    public TextStyleSpan.TextStyleRun getStyle() {
        return this.style;
    }

    public void onClick(View view) {
        Browser.openUrl(view.getContext(), Uri.parse(getURL()));
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(textPaint);
        }
        textPaint.setUnderlineText(true);
    }
}
