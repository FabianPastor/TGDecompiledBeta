package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$RLXwYYsRORY3jGEkK3Ou05-sCL4 implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ InputMedia f$1;
    private final /* synthetic */ DelayedMessage f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$RLXwYYsRORY3jGEkK3Ou05-sCL4(SendMessagesHelper sendMessagesHelper, InputMedia inputMedia, DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = inputMedia;
        this.f$2 = delayedMessage;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$uploadMultiMedia$21$SendMessagesHelper(this.f$1, this.f$2, tLObject, tL_error);
    }
}
