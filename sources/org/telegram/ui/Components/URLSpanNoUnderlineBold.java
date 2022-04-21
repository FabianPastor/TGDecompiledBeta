package org.telegram.ui.Components;

import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;

public class URLSpanNoUnderlineBold extends URLSpanNoUnderline {
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public URLSpanNoUnderlineBold(String url) {
        super(url != null ? url.replace(8238, ' ') : url);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        ds.setUnderlineText(false);
    }
}
