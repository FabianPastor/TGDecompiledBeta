package org.telegram.p005ui.Components;

import android.text.TextPaint;

/* renamed from: org.telegram.ui.Components.URLSpanUserMentionPhotoViewer */
public class URLSpanUserMentionPhotoViewer extends URLSpanUserMention {
    public URLSpanUserMentionPhotoViewer(String url, boolean isOutOwner) {
        super(url, 2);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(-1);
        ds.setUnderlineText(false);
    }
}
