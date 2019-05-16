package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EmojiView$GifSearchAdapter$T00z6XPCmLZsEfaqGzAOZW0Q-C8 implements Runnable {
    private final /* synthetic */ GifSearchAdapter f$0;
    private final /* synthetic */ TL_messages_getInlineBotResults f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ TLObject f$3;

    public /* synthetic */ -$$Lambda$EmojiView$GifSearchAdapter$T00z6XPCmLZsEfaqGzAOZW0Q-C8(GifSearchAdapter gifSearchAdapter, TL_messages_getInlineBotResults tL_messages_getInlineBotResults, String str, TLObject tLObject) {
        this.f$0 = gifSearchAdapter;
        this.f$1 = tL_messages_getInlineBotResults;
        this.f$2 = str;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$2$EmojiView$GifSearchAdapter(this.f$1, this.f$2, this.f$3);
    }
}
