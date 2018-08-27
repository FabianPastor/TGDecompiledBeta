package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$62 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$10;
    private final int arg$11;
    private final boolean arg$12;
    private final int arg$13;
    private final boolean arg$14;
    private final int arg$15;
    private final int arg$2;
    private final int arg$3;
    private final int arg$4;
    private final long arg$5;
    private final int arg$6;
    private final int arg$7;
    private final int arg$8;
    private final int arg$9;

    MessagesController$$Lambda$62(MessagesController messagesController, int i, int i2, int i3, long j, int i4, int i5, int i6, int i7, int i8, int i9, boolean z, int i10, boolean z2, int i11) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = i3;
        this.arg$5 = j;
        this.arg$6 = i4;
        this.arg$7 = i5;
        this.arg$8 = i6;
        this.arg$9 = i7;
        this.arg$10 = i8;
        this.arg$11 = i9;
        this.arg$12 = z;
        this.arg$13 = i10;
        this.arg$14 = z2;
        this.arg$15 = i11;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadMessagesInternal$87$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13, this.arg$14, this.arg$15, tLObject, tL_error);
    }
}
