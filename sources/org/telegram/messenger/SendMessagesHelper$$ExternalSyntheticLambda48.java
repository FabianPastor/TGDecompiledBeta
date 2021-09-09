package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ String f$6;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda48(SendMessagesHelper sendMessagesHelper, TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$Message;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = arrayList;
        this.f$5 = i2;
        this.f$6 = str;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$58(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
