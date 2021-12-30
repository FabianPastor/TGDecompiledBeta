package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputUser;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda317 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC$InputUser f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ Runnable f$7;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda317(MessagesController messagesController, boolean z, TLRPC$InputUser tLRPC$InputUser, long j, BaseFragment baseFragment, TLObject tLObject, boolean z2, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = tLRPC$InputUser;
        this.f$3 = j;
        this.f$4 = baseFragment;
        this.f$5 = tLObject;
        this.f$6 = z2;
        this.f$7 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$addUserToChat$231(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
    }
}
