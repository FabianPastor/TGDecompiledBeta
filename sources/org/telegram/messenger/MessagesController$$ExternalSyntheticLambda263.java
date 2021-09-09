package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getPeerDialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda263 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$10;
    public final /* synthetic */ int f$11;
    public final /* synthetic */ int f$12;
    public final /* synthetic */ int f$13;
    public final /* synthetic */ boolean f$14;
    public final /* synthetic */ boolean f$15;
    public final /* synthetic */ TLRPC$TL_messages_getPeerDialogs f$16;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda263(MessagesController messagesController, long j, long j2, boolean z, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, boolean z2, boolean z3, TLRPC$TL_messages_getPeerDialogs tLRPC$TL_messages_getPeerDialogs) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = z;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = i3;
        this.f$7 = i4;
        this.f$8 = i5;
        this.f$9 = i6;
        this.f$10 = i7;
        this.f$11 = i8;
        this.f$12 = i9;
        this.f$13 = i10;
        this.f$14 = z2;
        this.f$15 = z3;
        this.f$16 = tLRPC$TL_messages_getPeerDialogs;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController messagesController = this.f$0;
        MessagesController messagesController2 = messagesController;
        messagesController2.lambda$loadMessagesInternal$139(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, tLObject, tLRPC$TL_error);
    }
}
