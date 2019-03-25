package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$49 implements RequestDelegate {
    private final MessagesController arg$1;
    private final long arg$2;
    private final long arg$3;
    private final int arg$4;
    private final int arg$5;
    private final boolean arg$6;
    private final InputPeer arg$7;

    MessagesController$$Lambda$49(MessagesController messagesController, long j, long j2, int i, int i2, boolean z, InputPeer inputPeer) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = j2;
        this.arg$4 = i;
        this.arg$5 = i2;
        this.arg$6 = z;
        this.arg$7 = inputPeer;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$deleteDialog$69$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, tLObject, tL_error);
    }
}
