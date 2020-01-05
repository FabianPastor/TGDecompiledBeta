package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.BotInlineResult;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$VS0uMXi4Xu7RncTJ12bCTjhVRD8 implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ BotInlineResult f$1;
    private final /* synthetic */ AccountInstance f$2;
    private final /* synthetic */ HashMap f$3;
    private final /* synthetic */ MessageObject f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ int f$6;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$VS0uMXi4Xu7RncTJ12bCTjhVRD8(long j, BotInlineResult botInlineResult, AccountInstance accountInstance, HashMap hashMap, MessageObject messageObject, boolean z, int i) {
        this.f$0 = j;
        this.f$1 = botInlineResult;
        this.f$2 = accountInstance;
        this.f$3 = hashMap;
        this.f$4 = messageObject;
        this.f$5 = z;
        this.f$6 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingBotContextResult$58(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
