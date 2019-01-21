package org.telegram.ui.Components;

final /* synthetic */ class EmojiView$$Lambda$8 implements Runnable {
    private final EmojiView arg$1;
    private final int arg$2;

    EmojiView$$Lambda$8(EmojiView emojiView, int i) {
        this.arg$1 = emojiView;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$postBackspaceRunnable$10$EmojiView(this.arg$2);
    }
}
