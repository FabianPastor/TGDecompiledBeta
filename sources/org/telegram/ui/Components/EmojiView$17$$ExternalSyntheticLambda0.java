package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$17$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ EmojiView$17$$ExternalSyntheticLambda0 INSTANCE = new EmojiView$17$$ExternalSyntheticLambda0();

    private /* synthetic */ EmojiView$17$$ExternalSyntheticLambda0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        EmojiView.AnonymousClass17.lambda$sendReorder$0(tLObject, tLRPC$TL_error);
    }
}
