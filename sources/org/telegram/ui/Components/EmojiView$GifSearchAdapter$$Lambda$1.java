package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;

final /* synthetic */ class EmojiView$GifSearchAdapter$$Lambda$1 implements RequestDelegate {
    private final GifSearchAdapter arg$1;
    private final TL_messages_getInlineBotResults arg$2;
    private final String arg$3;

    EmojiView$GifSearchAdapter$$Lambda$1(GifSearchAdapter gifSearchAdapter, TL_messages_getInlineBotResults tL_messages_getInlineBotResults, String str) {
        this.arg$1 = gifSearchAdapter;
        this.arg$2 = tL_messages_getInlineBotResults;
        this.arg$3 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$search$3$EmojiView$GifSearchAdapter(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
