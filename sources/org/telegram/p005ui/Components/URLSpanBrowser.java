package org.telegram.p005ui.Components;

import android.net.Uri;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;

/* renamed from: org.telegram.ui.Components.URLSpanBrowser */
public class URLSpanBrowser extends URLSpan {
    public URLSpanBrowser(String url) {
        super(url);
    }

    public void onClick(View widget) {
        Browser.openUrl(widget.getContext(), Uri.parse(getURL()));
    }
}
