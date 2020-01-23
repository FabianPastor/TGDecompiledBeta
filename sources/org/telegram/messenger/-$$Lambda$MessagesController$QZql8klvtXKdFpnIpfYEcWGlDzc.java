package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$QZql8klvtXKdFpnIpfYEcWGlDzc implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_channels_inviteToChannel f$3;

    public /* synthetic */ -$$Lambda$MessagesController$QZql8klvtXKdFpnIpfYEcWGlDzc(MessagesController messagesController, TL_error tL_error, BaseFragment baseFragment, TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_channels_inviteToChannel;
    }

    public final void run() {
        this.f$0.lambda$null$182$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
