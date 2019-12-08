package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$ZisDATInJn1KRTC7Bc7jqwo7jm8 implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$ZisDATInJn1KRTC7Bc7jqwo7jm8(SendMessagesHelper sendMessagesHelper, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$15$SendMessagesHelper(this.f$1, tLObject, tL_error);
    }
}
