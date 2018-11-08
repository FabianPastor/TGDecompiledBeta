package org.telegram.p005ui.Components;

import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;

/* renamed from: org.telegram.ui.Components.URLSpanNoUnderlineBold */
public class URLSpanNoUnderlineBold extends URLSpanNoUnderline {
    public URLSpanNoUnderlineBold(String url) {
        super(url);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        ds.setUnderlineText(false);
    }
}
