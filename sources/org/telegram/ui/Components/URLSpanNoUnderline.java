package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;

public class URLSpanNoUnderline extends URLSpan {
    public URLSpanNoUnderline(String url) {
        super(url);
    }

    public void onClick(View widget) {
        String url = getURL();
        if (url.startsWith("@")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://t.me/");
            stringBuilder.append(url.substring(1));
            Browser.openUrl(widget.getContext(), Uri.parse(stringBuilder.toString()));
            return;
        }
        Browser.openUrl(widget.getContext(), url);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
