package org.telegram.ui.Components;

import org.telegram.ui.Components.AnimatedEmojiDrawable;

public final /* synthetic */ class AnimatedEmojiDrawable$EmojiImageReceiver$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ AnimatedEmojiDrawable.EmojiImageReceiver f$0;

    public /* synthetic */ AnimatedEmojiDrawable$EmojiImageReceiver$$ExternalSyntheticLambda0(AnimatedEmojiDrawable.EmojiImageReceiver emojiImageReceiver) {
        this.f$0 = emojiImageReceiver;
    }

    public final void run() {
        this.f$0.invalidate();
    }
}
