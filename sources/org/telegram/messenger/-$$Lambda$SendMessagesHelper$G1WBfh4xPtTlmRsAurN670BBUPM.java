package org.telegram.messenger;

import android.graphics.Bitmap;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_photo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$G1WBfh4xPtTlmRsAurN670BBUPM implements Runnable {
    private final /* synthetic */ TL_document f$0;
    private final /* synthetic */ Bitmap[] f$1;
    private final /* synthetic */ int f$10;
    private final /* synthetic */ TL_photo f$11;
    private final /* synthetic */ TL_game f$12;
    private final /* synthetic */ String[] f$2;
    private final /* synthetic */ AccountInstance f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ long f$5;
    private final /* synthetic */ MessageObject f$6;
    private final /* synthetic */ BotInlineResult f$7;
    private final /* synthetic */ HashMap f$8;
    private final /* synthetic */ boolean f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$G1WBfh4xPtTlmRsAurN670BBUPM(TL_document tL_document, Bitmap[] bitmapArr, String[] strArr, AccountInstance accountInstance, String str, long j, MessageObject messageObject, BotInlineResult botInlineResult, HashMap hashMap, boolean z, int i, TL_photo tL_photo, TL_game tL_game) {
        this.f$0 = tL_document;
        this.f$1 = bitmapArr;
        this.f$2 = strArr;
        this.f$3 = accountInstance;
        this.f$4 = str;
        this.f$5 = j;
        this.f$6 = messageObject;
        this.f$7 = botInlineResult;
        this.f$8 = hashMap;
        this.f$9 = z;
        this.f$10 = i;
        this.f$11 = tL_photo;
        this.f$12 = tL_game;
    }

    public final void run() {
        SendMessagesHelper.lambda$null$57(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
    }
}
