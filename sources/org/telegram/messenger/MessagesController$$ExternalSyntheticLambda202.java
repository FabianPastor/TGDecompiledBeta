package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda202 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC.TL_channels_editAdmin f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda202(MessagesController messagesController, long j, BaseFragment baseFragment, TLRPC.TL_channels_editAdmin tL_channels_editAdmin, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = baseFragment;
        this.f$3 = tL_channels_editAdmin;
        this.f$4 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m390x641ab1ec(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
