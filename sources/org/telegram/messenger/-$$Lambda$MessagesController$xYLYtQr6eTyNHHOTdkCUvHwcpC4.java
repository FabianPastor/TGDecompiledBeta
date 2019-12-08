package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_getBlocked;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$xYLYtQr6eTyNHHOTdkCUvHwcpC4 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ TL_contacts_getBlocked f$2;

    public /* synthetic */ -$$Lambda$MessagesController$xYLYtQr6eTyNHHOTdkCUvHwcpC4(MessagesController messagesController, boolean z, TL_contacts_getBlocked tL_contacts_getBlocked) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = tL_contacts_getBlocked;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$getBlockedUsers$59$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
