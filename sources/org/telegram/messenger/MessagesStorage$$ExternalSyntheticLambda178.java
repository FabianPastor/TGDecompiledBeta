package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_messages_messages;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda178 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$TL_messages_messages f$1;
    public final /* synthetic */ int f$10;
    public final /* synthetic */ int f$11;
    public final /* synthetic */ int f$12;
    public final /* synthetic */ int f$13;
    public final /* synthetic */ boolean f$14;
    public final /* synthetic */ boolean f$15;
    public final /* synthetic */ int f$16;
    public final /* synthetic */ int f$17;
    public final /* synthetic */ boolean f$18;
    public final /* synthetic */ int f$19;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$20;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ int f$9;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda178(MessagesStorage messagesStorage, TLRPC$TL_messages_messages tLRPC$TL_messages_messages, int i, long j, long j2, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, boolean z, boolean z2, int i11, int i12, boolean z3, int i13, boolean z4) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$TL_messages_messages;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = j2;
        this.f$5 = i2;
        this.f$6 = i3;
        this.f$7 = i4;
        this.f$8 = i5;
        this.f$9 = i6;
        this.f$10 = i7;
        this.f$11 = i8;
        this.f$12 = i9;
        this.f$13 = i10;
        this.f$14 = z;
        this.f$15 = z2;
        this.f$16 = i11;
        this.f$17 = i12;
        this.f$18 = z3;
        this.f$19 = i13;
        this.f$20 = z4;
    }

    public final void run() {
        MessagesStorage messagesStorage = this.f$0;
        MessagesStorage messagesStorage2 = messagesStorage;
        messagesStorage2.lambda$getMessagesInternal$129(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18, this.f$19, this.f$20);
    }
}
