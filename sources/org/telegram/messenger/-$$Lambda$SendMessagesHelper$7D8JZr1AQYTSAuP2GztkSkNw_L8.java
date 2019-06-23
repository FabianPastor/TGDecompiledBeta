package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_photo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$7D8JZr1AQYTSAuP2GztkSkNw_L8 implements Runnable {
    private final /* synthetic */ TL_document f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ MessageObject f$4;
    private final /* synthetic */ BotInlineResult f$5;
    private final /* synthetic */ HashMap f$6;
    private final /* synthetic */ TL_photo f$7;
    private final /* synthetic */ TL_game f$8;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$7D8JZr1AQYTSAuP2GztkSkNw_L8(TL_document tL_document, AccountInstance accountInstance, String str, long j, MessageObject messageObject, BotInlineResult botInlineResult, HashMap hashMap, TL_photo tL_photo, TL_game tL_game) {
        this.f$0 = tL_document;
        this.f$1 = accountInstance;
        this.f$2 = str;
        this.f$3 = j;
        this.f$4 = messageObject;
        this.f$5 = botInlineResult;
        this.f$6 = hashMap;
        this.f$7 = tL_photo;
        this.f$8 = tL_game;
    }

    public final void run() {
        SendMessagesHelper.lambda$null$49(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
