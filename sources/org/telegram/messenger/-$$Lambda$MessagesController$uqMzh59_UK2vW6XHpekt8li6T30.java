package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$uqMzh59_UK2vW6XHpekt8li6T30 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_channels_editBanned f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$MessagesController$uqMzh59_UK2vW6XHpekt8li6T30(MessagesController messagesController, TL_error tL_error, BaseFragment baseFragment, TL_channels_editBanned tL_channels_editBanned, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_channels_editBanned;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$null$41$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
