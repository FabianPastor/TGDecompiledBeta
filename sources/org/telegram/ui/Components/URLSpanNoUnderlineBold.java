package org.telegram.ui.Components;

import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;

public class URLSpanNoUnderlineBold extends URLSpanNoUnderline {
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public URLSpanNoUnderlineBold(String str) {
        super(str != null ? str.replace(8238, ' ') : str);
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint.setUnderlineText(false);
    }
}
