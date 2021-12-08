package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TextStyleSpan;

public class URLSpanMono extends MetricAffectingSpan {
    private int currentEnd;
    private CharSequence currentMessage;
    private int currentStart;
    private byte currentType;
    private TextStyleSpan.TextStyleRun style;

    public URLSpanMono(CharSequence message, int start, int end, byte type) {
        this(message, start, end, type, (TextStyleSpan.TextStyleRun) null);
    }

    public URLSpanMono(CharSequence message, int start, int end, byte type, TextStyleSpan.TextStyleRun run) {
        this.currentMessage = message;
        this.currentStart = start;
        this.currentEnd = end;
        this.currentType = type;
        this.style = run;
    }

    public void copyToClipboard() {
        AndroidUtilities.addToClipboard(this.currentMessage.subSequence(this.currentStart, this.currentEnd).toString());
    }

    public void updateMeasureState(TextPaint p) {
        p.setTextSize((float) AndroidUtilities.dp((float) (SharedConfig.fontSize - 1)));
        p.setFlags(p.getFlags() | 128);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
        } else {
            p.setTypeface(Typeface.MONOSPACE);
        }
    }

    public void updateDrawState(TextPaint p) {
        p.setTextSize((float) AndroidUtilities.dp((float) (SharedConfig.fontSize - 1)));
        byte b = this.currentType;
        if (b == 2) {
            p.setColor(-1);
        } else if (b == 1) {
            p.setColor(Theme.getColor("chat_messageTextOut"));
        } else {
            p.setColor(Theme.getColor("chat_messageTextIn"));
        }
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
            return;
        }
        p.setTypeface(Typeface.MONOSPACE);
        p.setUnderlineText(false);
    }
}
