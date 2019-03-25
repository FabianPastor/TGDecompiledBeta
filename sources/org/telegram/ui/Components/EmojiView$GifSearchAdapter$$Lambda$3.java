package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class EmojiView$GifSearchAdapter$$Lambda$3 implements Runnable {
    private final GifSearchAdapter arg$1;
    private final TLObject arg$2;

    EmojiView$GifSearchAdapter$$Lambda$3(GifSearchAdapter gifSearchAdapter, TLObject tLObject) {
        this.arg$1 = gifSearchAdapter;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$0$EmojiView$GifSearchAdapter(this.arg$2);
    }
}
