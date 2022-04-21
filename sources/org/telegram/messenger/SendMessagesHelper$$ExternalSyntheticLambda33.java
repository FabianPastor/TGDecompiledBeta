package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda33 implements Runnable {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ TLRPC.BotInlineResult f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ HashMap f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ int f$7;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda33(long j, TLRPC.BotInlineResult botInlineResult, AccountInstance accountInstance, HashMap hashMap, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        this.f$0 = j;
        this.f$1 = botInlineResult;
        this.f$2 = accountInstance;
        this.f$3 = hashMap;
        this.f$4 = messageObject;
        this.f$5 = messageObject2;
        this.f$6 = z;
        this.f$7 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingBotContextResult$80(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
