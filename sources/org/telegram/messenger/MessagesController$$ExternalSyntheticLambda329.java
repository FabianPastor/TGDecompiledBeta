package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputUser;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda329 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC$InputUser f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ Runnable f$5;
    public final /* synthetic */ Runnable f$6;
    public final /* synthetic */ BaseFragment f$7;
    public final /* synthetic */ TLObject f$8;
    public final /* synthetic */ boolean f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda329(MessagesController messagesController, boolean z, TLRPC$InputUser tLRPC$InputUser, long j, boolean z2, Runnable runnable, Runnable runnable2, BaseFragment baseFragment, TLObject tLObject, boolean z3) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = tLRPC$InputUser;
        this.f$3 = j;
        this.f$4 = z2;
        this.f$5 = runnable;
        this.f$6 = runnable2;
        this.f$7 = baseFragment;
        this.f$8 = tLObject;
        this.f$9 = z3;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$addUserToChat$237(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tLRPC$TL_error);
    }
}
