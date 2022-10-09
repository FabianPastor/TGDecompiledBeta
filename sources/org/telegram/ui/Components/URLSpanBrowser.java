package org.telegram.ui.Components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.TextStyleSpan;
/* loaded from: classes3.dex */
public class URLSpanBrowser extends URLSpan {
    private TextStyleSpan.TextStyleRun style;

    public URLSpanBrowser(String str, TextStyleSpan.TextStyleRun textStyleRun) {
        super(str != null ? str.replace((char) 8238, ' ') : str);
        this.style = textStyleRun;
    }

    public TextStyleSpan.TextStyleRun getStyle() {
        return this.style;
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View view) {
        Browser.openUrl(view.getContext(), Uri.parse(getURL()));
    }

    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(textPaint);
        }
        textPaint.setUnderlineText(true);
    }
}
