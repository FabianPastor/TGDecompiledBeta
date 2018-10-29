package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.BotInlineResult;

final /* synthetic */ class SendMessagesHelper$$Lambda$17 implements Runnable {
    private final BotInlineResult arg$1;
    private final long arg$2;
    private final int arg$3;
    private final HashMap arg$4;
    private final MessageObject arg$5;

    SendMessagesHelper$$Lambda$17(BotInlineResult botInlineResult, long j, int i, HashMap hashMap, MessageObject messageObject) {
        this.arg$1 = botInlineResult;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = hashMap;
        this.arg$5 = messageObject;
    }

    public void run() {
        SendMessagesHelper.lambda$prepareSendingBotContextResult$49$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
