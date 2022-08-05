package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ TLRPC$BotInlineResult f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ HashMap f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ MessageObject f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ int f$8;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda4(long j, TLRPC$BotInlineResult tLRPC$BotInlineResult, AccountInstance accountInstance, HashMap hashMap, BaseFragment baseFragment, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        this.f$0 = j;
        this.f$1 = tLRPC$BotInlineResult;
        this.f$2 = accountInstance;
        this.f$3 = hashMap;
        this.f$4 = baseFragment;
        this.f$5 = messageObject;
        this.f$6 = messageObject2;
        this.f$7 = z;
        this.f$8 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingBotContextResult$82(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
