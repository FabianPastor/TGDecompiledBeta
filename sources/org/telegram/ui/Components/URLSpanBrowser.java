package org.telegram.ui.Components;

import android.net.Uri;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;

public class URLSpanBrowser extends URLSpan {
    public URLSpanBrowser(String str) {
        super(str);
    }

    public void onClick(View view) {
        Browser.openUrl(view.getContext(), Uri.parse(getURL()));
    }
}
