package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ MessagesController.ErrorDelegate f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLObject f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ TLRPC.InputUser f$7;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda32(MessagesController messagesController, MessagesController.ErrorDelegate errorDelegate, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, boolean z, boolean z2, TLRPC.InputUser inputUser) {
        this.f$0 = messagesController;
        this.f$1 = errorDelegate;
        this.f$2 = tL_error;
        this.f$3 = baseFragment;
        this.f$4 = tLObject;
        this.f$5 = z;
        this.f$6 = z2;
        this.f$7 = inputUser;
    }

    public final void run() {
        this.f$0.m122x976101f0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
