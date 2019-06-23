package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.BotInlineResult;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$RlwhLE8SG2BzLNJECNyLkrGcsXs implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ BotInlineResult f$1;
    private final /* synthetic */ AccountInstance f$2;
    private final /* synthetic */ HashMap f$3;
    private final /* synthetic */ MessageObject f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$RlwhLE8SG2BzLNJECNyLkrGcsXs(long j, BotInlineResult botInlineResult, AccountInstance accountInstance, HashMap hashMap, MessageObject messageObject) {
        this.f$0 = j;
        this.f$1 = botInlineResult;
        this.f$2 = accountInstance;
        this.f$3 = hashMap;
        this.f$4 = messageObject;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingBotContextResult$50(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
