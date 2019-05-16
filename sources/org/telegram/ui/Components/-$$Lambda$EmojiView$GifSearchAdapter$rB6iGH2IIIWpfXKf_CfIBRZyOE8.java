package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EmojiView$GifSearchAdapter$rB6iGH2IIIWpfXKf_CfIBRZyOE8 implements RequestDelegate {
    private final /* synthetic */ GifSearchAdapter f$0;

    public /* synthetic */ -$$Lambda$EmojiView$GifSearchAdapter$rB6iGH2IIIWpfXKf_CfIBRZyOE8(GifSearchAdapter gifSearchAdapter) {
        this.f$0 = gifSearchAdapter;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchBotUser$1$EmojiView$GifSearchAdapter(tLObject, tL_error);
    }
}
