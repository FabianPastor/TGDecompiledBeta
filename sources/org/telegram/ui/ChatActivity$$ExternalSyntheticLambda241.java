package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda241 implements RequestDelegate {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda241 INSTANCE = new ChatActivity$$ExternalSyntheticLambda241();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda241() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChatActivity.lambda$markSponsoredAsRead$253(tLObject, tLRPC$TL_error);
    }
}
