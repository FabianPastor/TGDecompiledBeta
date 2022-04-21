package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda27 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ TLRPC.Message f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ String f$6;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda27(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, MessageObject messageObject, TLRPC.Message message, int i, boolean z, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = arrayList;
        this.f$2 = messageObject;
        this.f$3 = message;
        this.f$4 = i;
        this.f$5 = z;
        this.f$6 = str;
    }

    public final void run() {
        this.f$0.m449x6117d38c(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
