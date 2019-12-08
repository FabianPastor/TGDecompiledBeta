package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan.TextStyleRun;

public class URLSpanBrowser extends URLSpan {
    private TextStyleRun style;

    public URLSpanBrowser(String str) {
        this(str, null);
    }

    public URLSpanBrowser(String str, TextStyleRun textStyleRun) {
        if (str != null) {
            str = str.replace(8238, ' ');
        }
        super(str);
        this.style = textStyleRun;
    }

    public TextStyleRun getStyle() {
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
        textPaint.setUnderlineText(true);
    }
}
