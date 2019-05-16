package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$xQRkDsNdc7E8A8vOKZnj4SaYK-I implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_channels_editAdmin f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$MessagesController$xQRkDsNdc7E8A8vOKZnj4SaYK-I(MessagesController messagesController, TL_error tL_error, BaseFragment baseFragment, TL_channels_editAdmin tL_channels_editAdmin, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_channels_editAdmin;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$null$47$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
