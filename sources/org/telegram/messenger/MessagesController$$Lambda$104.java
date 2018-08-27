package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$104 implements RequestDelegate {
    private final MessagesController arg$1;
    private final boolean arg$2;
    private final InputUser arg$3;
    private final int arg$4;
    private final BaseFragment arg$5;
    private final TLObject arg$6;
    private final boolean arg$7;

    MessagesController$$Lambda$104(MessagesController messagesController, boolean z, InputUser inputUser, int i, BaseFragment baseFragment, TLObject tLObject, boolean z2) {
        this.arg$1 = messagesController;
        this.arg$2 = z;
        this.arg$3 = inputUser;
        this.arg$4 = i;
        this.arg$5 = baseFragment;
        this.arg$6 = tLObject;
        this.arg$7 = z2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$addUserToChat$161$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, tLObject, tL_error);
    }
}
