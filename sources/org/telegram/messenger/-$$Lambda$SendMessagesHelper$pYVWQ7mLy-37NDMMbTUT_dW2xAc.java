package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.BotInlineResult;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$pYVWQ7mLy-37NDMMbTUT_dW2xAc implements Runnable {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ BotInlineResult f$1;
    private final /* synthetic */ AccountInstance f$2;
    private final /* synthetic */ HashMap f$3;
    private final /* synthetic */ MessageObject f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$pYVWQ7mLy-37NDMMbTUT_dW2xAc(long j, BotInlineResult botInlineResult, AccountInstance accountInstance, HashMap hashMap, MessageObject messageObject) {
        this.f$0 = j;
        this.f$1 = botInlineResult;
        this.f$2 = accountInstance;
        this.f$3 = hashMap;
        this.f$4 = messageObject;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingBotContextResult$51(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
