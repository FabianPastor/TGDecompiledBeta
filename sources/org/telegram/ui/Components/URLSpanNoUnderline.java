package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.Components.TextStyleSpan;

public class URLSpanNoUnderline extends URLSpan {
    private boolean forceNoUnderline;
    public String label;
    private TLObject object;
    private TextStyleSpan.TextStyleRun style;

    public URLSpanNoUnderline(String url) {
        this(url, (TextStyleSpan.TextStyleRun) null);
    }

    public URLSpanNoUnderline(String url, boolean forceNoUnderline2) {
        this(url, (TextStyleSpan.TextStyleRun) null);
        this.forceNoUnderline = forceNoUnderline2;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public URLSpanNoUnderline(String url, TextStyleSpan.TextStyleRun run) {
        super(url != null ? url.replace(8238, ' ') : url);
        this.forceNoUnderline = false;
        this.style = run;
    }

    public void onClick(View widget) {
        String url = getURL();
        if (url.startsWith("@")) {
            Browser.openUrl(widget.getContext(), Uri.parse("https://t.me/" + url.substring(1)));
            return;
        }
        Browser.openUrl(widget.getContext(), url);
    }

    public void updateDrawState(TextPaint p) {
        int l = p.linkColor;
        int c = p.getColor();
        super.updateDrawState(p);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
        }
        p.setUnderlineText(l == c && !this.forceNoUnderline);
    }

    public void setObject(TLObject spanObject) {
        this.object = spanObject;
    }

    public TLObject getObject() {
        return this.object;
    }
}
