package org.telegram.ui.Components;

import android.text.TextPaint;

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
