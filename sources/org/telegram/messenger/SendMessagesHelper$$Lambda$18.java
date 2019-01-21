package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.BotInlineResult;

final /* synthetic */ class SendMessagesHelper$$Lambda$18 implements Runnable {
    private final long arg$1;
    private final BotInlineResult arg$2;
    private final int arg$3;
    private final HashMap arg$4;
    private final MessageObject arg$5;

    SendMessagesHelper$$Lambda$18(long j, BotInlineResult botInlineResult, int i, HashMap hashMap, MessageObject messageObject) {
        this.arg$1 = j;
        this.arg$2 = botInlineResult;
        this.arg$3 = i;
        this.arg$4 = hashMap;
        this.arg$5 = messageObject;
    }

    public void run() {
        SendMessagesHelper.lambda$prepareSendingBotContextResult$50$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
