package org.telegram.p005ui.Components;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/* renamed from: org.telegram.ui.Components.EmojiView$$Lambda$7 */
final /* synthetic */ class EmojiView$$Lambda$7 implements OnKeyListener {
    private final EmojiView arg$1;

    EmojiView$$Lambda$7(EmojiView emojiView) {
        this.arg$1 = emojiView;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$9$EmojiView(view, i, keyEvent);
    }
}
