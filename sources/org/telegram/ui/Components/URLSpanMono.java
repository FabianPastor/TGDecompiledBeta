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

    public URLSpanMono(CharSequence charSequence, int i, int i2, byte b) {
        this.currentMessage = charSequence;
        this.currentStart = i;
        this.currentEnd = i2;
        this.currentType = b;
    }

    public void copyToClipboard() {
        AndroidUtilities.addToClipboard(this.currentMessage.subSequence(this.currentStart, this.currentEnd).toString());
    }

    public void updateMeasureState(TextPaint textPaint) {
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setTextSize((float) AndroidUtilities.dp((float) (SharedConfig.fontSize - 1)));
        textPaint.setFlags(textPaint.getFlags() | 128);
    }

    public void updateDrawState(TextPaint textPaint) {
        textPaint.setTextSize((float) AndroidUtilities.dp((float) (SharedConfig.fontSize - 1)));
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setUnderlineText(false);
        if (this.currentType == (byte) 2) {
            textPaint.setColor(-1);
        } else if (this.currentType == (byte) 1) {
            textPaint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
        } else {
            textPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
        }
    }
}
