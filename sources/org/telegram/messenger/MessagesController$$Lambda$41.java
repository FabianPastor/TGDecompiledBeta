package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$41 implements RequestDelegate {
    private final MessagesController arg$1;
    private final long arg$2;
    private final boolean arg$3;
    private final long arg$4;

    MessagesController$$Lambda$41(MessagesController messagesController, long j, boolean z, long j2) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = z;
        this.arg$4 = j2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$saveWallpaperToServer$60$MessagesController(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
