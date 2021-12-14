package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$Message f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ TLObject f$7;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda57(SendMessagesHelper sendMessagesHelper, TLRPC$TL_error tLRPC$TL_error, TLRPC$Message tLRPC$Message, TLObject tLObject, MessageObject messageObject, String str, boolean z, TLObject tLObject2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$Message;
        this.f$3 = tLObject;
        this.f$4 = messageObject;
        this.f$5 = str;
        this.f$6 = z;
        this.f$7 = tLObject2;
    }

    public final void run() {
        this.f$0.lambda$performSendMessageRequest$50(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
