package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda258 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$10;
    public final /* synthetic */ int f$11;
    public final /* synthetic */ int f$12;
    public final /* synthetic */ int f$13;
    public final /* synthetic */ int f$14;
    public final /* synthetic */ boolean f$15;
    public final /* synthetic */ int f$16;
    public final /* synthetic */ boolean f$17;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda258(MessagesController messagesController, int i, int i2, long j, long j2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, boolean z, int i13, boolean z2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = j;
        this.f$4 = j2;
        this.f$5 = i3;
        this.f$6 = i4;
        this.f$7 = i5;
        this.f$8 = i6;
        this.f$9 = i7;
        this.f$10 = i8;
        this.f$11 = i9;
        this.f$12 = i10;
        this.f$13 = i11;
        this.f$14 = i12;
        this.f$15 = z;
        this.f$16 = i13;
        this.f$17 = z2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController messagesController = this.f$0;
        MessagesController messagesController2 = messagesController;
        messagesController2.lambda$loadMessagesInternal$144(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, tLObject, tLRPC$TL_error);
    }
}
