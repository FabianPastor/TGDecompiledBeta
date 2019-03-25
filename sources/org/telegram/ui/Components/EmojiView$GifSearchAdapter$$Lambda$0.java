package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class EmojiView$GifSearchAdapter$$Lambda$0 implements RequestDelegate {
    private final GifSearchAdapter arg$1;

    EmojiView$GifSearchAdapter$$Lambda$0(GifSearchAdapter gifSearchAdapter) {
        this.arg$1 = gifSearchAdapter;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchBotUser$1$EmojiView$GifSearchAdapter(tLObject, tL_error);
    }
}
