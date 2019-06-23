package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$3WnOhoffzjjGGcQAUThhfqYJdSc implements RequestDelegate {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$3WnOhoffzjjGGcQAUThhfqYJdSc(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$startEditingMessageObject$69$ChatActivity(tLObject, tL_error);
    }
}
