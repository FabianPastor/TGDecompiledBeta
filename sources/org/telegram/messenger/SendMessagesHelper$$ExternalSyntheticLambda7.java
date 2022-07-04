package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ boolean f$10;
    public final /* synthetic */ int f$11;
    public final /* synthetic */ TLRPC.TL_photo f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ SendMessagesHelper.SendingMediaInfo f$4;
    public final /* synthetic */ HashMap f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ long f$7;
    public final /* synthetic */ MessageObject f$8;
    public final /* synthetic */ MessageObject f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda7(MessageObject messageObject, AccountInstance accountInstance, TLRPC.TL_photo tL_photo, boolean z, SendMessagesHelper.SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, boolean z2, int i) {
        this.f$0 = messageObject;
        this.f$1 = accountInstance;
        this.f$2 = tL_photo;
        this.f$3 = z;
        this.f$4 = sendingMediaInfo;
        this.f$5 = hashMap;
        this.f$6 = str;
        this.f$7 = j;
        this.f$8 = messageObject2;
        this.f$9 = messageObject3;
        this.f$10 = z2;
        this.f$11 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingMedia$86(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
    }
}
