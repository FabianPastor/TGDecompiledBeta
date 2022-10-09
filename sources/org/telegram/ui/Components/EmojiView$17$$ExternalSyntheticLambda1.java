package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.EmojiView;
/* loaded from: classes3.dex */
public final /* synthetic */ class EmojiView$17$$ExternalSyntheticLambda1 implements RequestDelegate {
    public static final /* synthetic */ EmojiView$17$$ExternalSyntheticLambda1 INSTANCE = new EmojiView$17$$ExternalSyntheticLambda1();

    private /* synthetic */ EmojiView$17$$ExternalSyntheticLambda1() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        EmojiView.AnonymousClass17.lambda$sendReorder$1(tLObject, tLRPC$TL_error);
    }
}
