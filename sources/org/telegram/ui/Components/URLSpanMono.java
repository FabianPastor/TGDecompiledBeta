package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public class URLSpanMono extends MetricAffectingSpan {
    private int currentEnd;
    private CharSequence currentMessage;
    private int currentStart;
    private byte currentType;

    public URLSpanMono(CharSequence message, int start, int end, byte type) {
        this.currentMessage = message;
        this.currentStart = start;
        this.currentEnd = end;
        this.currentType = type;
    }

    public void copyToClipboard() {
        AndroidUtilities.addToClipboard(this.currentMessage.subSequence(this.currentStart, this.currentEnd).toString());
    }

    public void updateMeasureState(TextPaint p) {
        p.setTypeface(Typeface.MONOSPACE);
        p.setTextSize((float) AndroidUtilities.dp((float) (SharedConfig.fontSize - 1)));
        p.setFlags(p.getFlags() | 128);
    }

    public void updateDrawState(TextPaint ds) {
        ds.setTextSize((float) AndroidUtilities.dp((float) (SharedConfig.fontSize - 1)));
        ds.setTypeface(Typeface.MONOSPACE);
        ds.setUnderlineText(false);
        if (this.currentType == (byte) 2) {
            ds.setColor(-1);
        } else if (this.currentType == (byte) 1) {
            ds.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
        } else {
            ds.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
        }
    }
}
