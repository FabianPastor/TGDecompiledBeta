package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda291 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Runnable f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLRPC$TL_channels_editAdmin f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ Runnable f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda291(MessagesController messagesController, long j, Runnable runnable, BaseFragment baseFragment, TLRPC$TL_channels_editAdmin tLRPC$TL_channels_editAdmin, boolean z, Runnable runnable2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = runnable;
        this.f$3 = baseFragment;
        this.f$4 = tLRPC$TL_channels_editAdmin;
        this.f$5 = z;
        this.f$6 = runnable2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$setUserAdminRole$79(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
    }
}
