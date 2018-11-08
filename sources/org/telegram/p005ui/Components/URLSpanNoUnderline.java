package org.telegram.p005ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;

/* renamed from: org.telegram.ui.Components.URLSpanNoUnderline */
public class URLSpanNoUnderline extends URLSpan {
    public URLSpanNoUnderline(String url) {
        super(url);
    }

    public void onClick(View widget) {
        String url = getURL();
        if (url.startsWith("@")) {
            Browser.openUrl(widget.getContext(), Uri.parse("https://t.me/" + url.substring(1)));
            return;
        }
        Browser.openUrl(widget.getContext(), url);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
