package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC.TL_channels_editBanned f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda57(MessagesController messagesController, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_editBanned tL_channels_editBanned, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_channels_editBanned;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.m386x4d09d401(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
