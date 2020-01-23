package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$KKGu_msivzZO3k082-Iog0YGAb0 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ InputUser f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MessagesController$KKGu_msivzZO3k082-Iog0YGAb0(MessagesController messagesController, boolean z, InputUser inputUser, int i) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = inputUser;
        this.f$3 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$deleteUserFromChat$199$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
