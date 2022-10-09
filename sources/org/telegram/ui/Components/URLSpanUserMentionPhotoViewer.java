package org.telegram.ui.Components;

import android.text.TextPaint;
/* loaded from: classes3.dex */
public class URLSpanUserMentionPhotoViewer extends URLSpanUserMention {
    public URLSpanUserMentionPhotoViewer(String str, boolean z) {
        super(str, 2);
    }

    @Override // org.telegram.ui.Components.URLSpanUserMention, org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setColor(-1);
        textPaint.setUnderlineText(false);
    }
}
