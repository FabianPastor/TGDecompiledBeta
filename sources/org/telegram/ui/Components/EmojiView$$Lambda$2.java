package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class EmojiView$$Lambda$2 implements OnTouchListener {
    private final EmojiView arg$1;

    EmojiView$$Lambda$2(EmojiView emojiView) {
        this.arg$1 = emojiView;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$new$3$EmojiView(view, motionEvent);
    }
}
