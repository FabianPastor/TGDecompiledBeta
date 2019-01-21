package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class EmojiView$$Lambda$0 implements OnTouchListener {
    private final EmojiView arg$1;

    EmojiView$$Lambda$0(EmojiView emojiView) {
        this.arg$1 = emojiView;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$new$1$EmojiView(view, motionEvent);
    }
}
