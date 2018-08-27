package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$61 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$10;
    private final int arg$11;
    private final int arg$12;
    private final boolean arg$13;
    private final long arg$2;
    private final int arg$3;
    private final int arg$4;
    private final int arg$5;
    private final int arg$6;
    private final int arg$7;
    private final int arg$8;
    private final boolean arg$9;

    MessagesController$$Lambda$61(MessagesController messagesController, long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7, int i8, int i9, boolean z2) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = i2;
        this.arg$5 = i3;
        this.arg$6 = i4;
        this.arg$7 = i5;
        this.arg$8 = i6;
        this.arg$9 = z;
        this.arg$10 = i7;
        this.arg$11 = i8;
        this.arg$12 = i9;
        this.arg$13 = z2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadMessagesInternal$86$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13, tLObject, tL_error);
    }
}
