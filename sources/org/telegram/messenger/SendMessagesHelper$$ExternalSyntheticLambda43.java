package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda43 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ int f$6;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda43(SendMessagesHelper sendMessagesHelper, TLRPC.Message message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = arrayList;
        this.f$5 = j;
        this.f$6 = i2;
    }

    public final void run() {
        this.f$0.m449x16641999(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
