package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$oWtR0AbMdP04znHWUTItf-tR4Wc implements Runnable {
    private final /* synthetic */ MessageObject f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_document f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ HashMap f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ long f$6;
    private final /* synthetic */ MessageObject f$7;
    private final /* synthetic */ String f$8;
    private final /* synthetic */ ArrayList f$9;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$oWtR0AbMdP04znHWUTItf-tR4Wc(MessageObject messageObject, int i, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, String str3, ArrayList arrayList) {
        this.f$0 = messageObject;
        this.f$1 = i;
        this.f$2 = tL_document;
        this.f$3 = str;
        this.f$4 = hashMap;
        this.f$5 = str2;
        this.f$6 = j;
        this.f$7 = messageObject2;
        this.f$8 = str3;
        this.f$9 = arrayList;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingDocumentInternal$44(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
