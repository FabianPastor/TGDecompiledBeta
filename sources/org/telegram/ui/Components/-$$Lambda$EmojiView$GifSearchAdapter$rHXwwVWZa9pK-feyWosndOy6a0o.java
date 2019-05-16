package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EmojiView$GifSearchAdapter$rHXwwVWZa9pK-feyWosndOy6a0o implements RequestDelegate {
    private final /* synthetic */ GifSearchAdapter f$0;
    private final /* synthetic */ TL_messages_getInlineBotResults f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$EmojiView$GifSearchAdapter$rHXwwVWZa9pK-feyWosndOy6a0o(GifSearchAdapter gifSearchAdapter, TL_messages_getInlineBotResults tL_messages_getInlineBotResults, String str) {
        this.f$0 = gifSearchAdapter;
        this.f$1 = tL_messages_getInlineBotResults;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$search$3$EmojiView$GifSearchAdapter(this.f$1, this.f$2, tLObject, tL_error);
    }
}
