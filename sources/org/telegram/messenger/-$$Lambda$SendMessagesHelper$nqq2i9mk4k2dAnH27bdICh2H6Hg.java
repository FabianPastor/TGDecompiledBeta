package org.telegram.messenger;

import java.util.HashMap;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.tgnet.TLRPC.TL_document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$nqq2i9mk4k2dAnH27bdICh2H6Hg implements Runnable {
    private final /* synthetic */ MessageObject f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ int f$10;
    private final /* synthetic */ TL_document f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ HashMap f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ long f$6;
    private final /* synthetic */ MessageObject f$7;
    private final /* synthetic */ SendingMediaInfo f$8;
    private final /* synthetic */ boolean f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$nqq2i9mk4k2dAnH27bdICh2H6Hg(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        this.f$0 = messageObject;
        this.f$1 = accountInstance;
        this.f$2 = tL_document;
        this.f$3 = str;
        this.f$4 = hashMap;
        this.f$5 = str2;
        this.f$6 = j;
        this.f$7 = messageObject2;
        this.f$8 = sendingMediaInfo;
        this.f$9 = z;
        this.f$10 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$null$67(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}