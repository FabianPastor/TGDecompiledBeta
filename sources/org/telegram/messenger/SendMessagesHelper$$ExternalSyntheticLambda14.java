package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC$TL_document;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ boolean f$10;
    public final /* synthetic */ int f$11;
    public final /* synthetic */ TLRPC$TL_document f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ HashMap f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ MessageObject f$7;
    public final /* synthetic */ MessageObject f$8;
    public final /* synthetic */ String f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda14(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, MessageObject messageObject4, String str2, boolean z, int i) {
        this.f$0 = messageObject;
        this.f$1 = accountInstance;
        this.f$2 = tLRPC$TL_document;
        this.f$3 = messageObject2;
        this.f$4 = hashMap;
        this.f$5 = str;
        this.f$6 = j;
        this.f$7 = messageObject3;
        this.f$8 = messageObject4;
        this.f$9 = str2;
        this.f$10 = z;
        this.f$11 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingAudioDocuments$74(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
    }
}
