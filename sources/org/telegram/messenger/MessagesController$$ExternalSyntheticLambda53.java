package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda53 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ TLRPC.InputUser f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda53(MessagesController messagesController, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2, TLRPC.InputUser inputUser) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tLObject;
        this.f$4 = z;
        this.f$5 = z2;
        this.f$6 = inputUser;
    }

    public final void run() {
        this.f$0.m119xa68dea36(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
