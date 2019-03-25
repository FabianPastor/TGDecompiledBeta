package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;

final /* synthetic */ class EmojiView$GifSearchAdapter$$Lambda$2 implements Runnable {
    private final GifSearchAdapter arg$1;
    private final TL_messages_getInlineBotResults arg$2;
    private final String arg$3;
    private final TLObject arg$4;

    EmojiView$GifSearchAdapter$$Lambda$2(GifSearchAdapter gifSearchAdapter, TL_messages_getInlineBotResults tL_messages_getInlineBotResults, String str, TLObject tLObject) {
        this.arg$1 = gifSearchAdapter;
        this.arg$2 = tL_messages_getInlineBotResults;
        this.arg$3 = str;
        this.arg$4 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$2$EmojiView$GifSearchAdapter(this.arg$2, this.arg$3, this.arg$4);
    }
}
