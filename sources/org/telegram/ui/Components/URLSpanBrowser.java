package org.telegram.ui.Components;

import android.net.Uri;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;

public class URLSpanBrowser extends URLSpan {
    public URLSpanBrowser(String url) {
        super(url);
    }

    public void onClick(View widget) {
        Browser.openUrl(widget.getContext(), Uri.parse(getURL()));
    }
}
