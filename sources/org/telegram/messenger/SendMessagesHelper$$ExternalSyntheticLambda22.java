package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$Message f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda22(SendMessagesHelper sendMessagesHelper, int i, TLRPC$Message tLRPC$Message, ArrayList arrayList, MessageObject messageObject, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = i;
        this.f$2 = tLRPC$Message;
        this.f$3 = arrayList;
        this.f$4 = messageObject;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$sendMessage$9(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}