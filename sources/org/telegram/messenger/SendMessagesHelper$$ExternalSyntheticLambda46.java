package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda46 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ TLRPC.Peer f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ TLRPC.Message f$7;
    public final /* synthetic */ int f$8;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda46(SendMessagesHelper sendMessagesHelper, TLRPC.Message message, TLRPC.Peer peer, int i, int i2, ArrayList arrayList, long j, TLRPC.Message message2, int i3) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = peer;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = arrayList;
        this.f$6 = j;
        this.f$7 = message2;
        this.f$8 = i3;
    }

    public final void run() {
        this.f$0.m479lambda$sendMessage$11$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
