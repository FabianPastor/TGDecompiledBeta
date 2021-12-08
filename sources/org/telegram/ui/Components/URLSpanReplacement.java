package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan;

public class URLSpanReplacement extends URLSpan {
    private TextStyleSpan.TextStyleRun style;

    public URLSpanReplacement(String url) {
        this(url, (TextStyleSpan.TextStyleRun) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public URLSpanReplacement(String url, TextStyleSpan.TextStyleRun run) {
        super(url != null ? url.replace(8238, ' ') : url);
        this.style = run;
    }

    public TextStyleSpan.TextStyleRun getTextStyleRun() {
        return this.style;
    }

    public void onClick(View widget) {
        Browser.openUrl(widget.getContext(), Uri.parse(getURL()));
    }

    public void updateDrawState(TextPaint p) {
        int color = p.getColor();
        super.updateDrawState(p);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
            p.setUnderlineText(p.linkColor == color);
        }
    }
}
