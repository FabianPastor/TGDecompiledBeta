package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$DTycE95B3vS8QWCwwNi_-eudjcQ implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ int f$10;
    private final /* synthetic */ boolean f$11;
    private final /* synthetic */ int f$12;
    private final /* synthetic */ boolean f$13;
    private final /* synthetic */ int f$14;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ int f$7;
    private final /* synthetic */ int f$8;
    private final /* synthetic */ int f$9;

    public /* synthetic */ -$$Lambda$MessagesController$DTycE95B3vS8QWCwwNi_-eudjcQ(MessagesController messagesController, long j, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z, int i10, boolean z2, int i11) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = i3;
        this.f$5 = i4;
        this.f$6 = i5;
        this.f$7 = i6;
        this.f$8 = i7;
        this.f$9 = i8;
        this.f$10 = i9;
        this.f$11 = z;
        this.f$12 = i10;
        this.f$13 = z2;
        this.f$14 = i11;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        TLObject tLObject2 = tLObject;
        TL_error tL_error2 = tL_error;
        MessagesController messagesController = this.f$0;
        MessagesController messagesController2 = messagesController;
        messagesController2.lambda$loadMessagesInternal$118$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, tLObject2, tL_error2);
    }
}
