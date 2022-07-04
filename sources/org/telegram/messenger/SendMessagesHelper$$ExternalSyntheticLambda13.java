package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC$TL_document;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ ArrayList f$10;
    public final /* synthetic */ boolean f$11;
    public final /* synthetic */ int f$12;
    public final /* synthetic */ TLRPC$TL_document f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ HashMap f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ long f$6;
    public final /* synthetic */ MessageObject f$7;
    public final /* synthetic */ MessageObject f$8;
    public final /* synthetic */ String f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda13(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, String str3, ArrayList arrayList, boolean z, int i) {
        this.f$0 = messageObject;
        this.f$1 = accountInstance;
        this.f$2 = tLRPC$TL_document;
        this.f$3 = str;
        this.f$4 = hashMap;
        this.f$5 = str2;
        this.f$6 = j;
        this.f$7 = messageObject2;
        this.f$8 = messageObject3;
        this.f$9 = str3;
        this.f$10 = arrayList;
        this.f$11 = z;
        this.f$12 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingDocumentInternal$73(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
