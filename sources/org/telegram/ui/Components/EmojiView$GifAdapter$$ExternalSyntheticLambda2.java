package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$GifAdapter$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ EmojiView.GifAdapter f$0;

    public /* synthetic */ EmojiView$GifAdapter$$ExternalSyntheticLambda2(EmojiView.GifAdapter gifAdapter) {
        this.f$0 = gifAdapter;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchBotUser$1(tLObject, tLRPC$TL_error);
    }
}
