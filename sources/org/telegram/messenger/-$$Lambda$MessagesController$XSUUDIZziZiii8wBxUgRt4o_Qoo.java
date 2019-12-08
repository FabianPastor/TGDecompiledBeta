package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$XSUUDIZziZiii8wBxUgRt4o_Qoo implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_channels_editAdmin f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$MessagesController$XSUUDIZziZiii8wBxUgRt4o_Qoo(MessagesController messagesController, int i, BaseFragment baseFragment, TL_channels_editAdmin tL_channels_editAdmin, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = baseFragment;
        this.f$3 = tL_channels_editAdmin;
        this.f$4 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$setUserAdminRole$58$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
