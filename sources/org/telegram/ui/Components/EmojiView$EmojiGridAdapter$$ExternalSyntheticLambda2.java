package org.telegram.ui.Components;

import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ EmojiView.EmojiPackButton f$0;

    public /* synthetic */ EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda2(EmojiView.EmojiPackButton emojiPackButton) {
        this.f$0 = emojiPackButton;
    }

    public final void run() {
        this.f$0.updateInstall(true, true);
    }
}
