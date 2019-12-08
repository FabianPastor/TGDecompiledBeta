package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$krA7rlrAczwuoflozUuOTFM-p-I implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ Runnable f$3;

    public /* synthetic */ -$$Lambda$MessagesController$krA7rlrAczwuoflozUuOTFM-p-I(MessagesController messagesController, int i, int i2, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = runnable;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadDialogs$128$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
