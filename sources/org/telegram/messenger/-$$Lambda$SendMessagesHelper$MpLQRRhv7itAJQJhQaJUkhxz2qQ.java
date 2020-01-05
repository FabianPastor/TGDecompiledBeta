package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$MpLQRRhv7itAJQJhQaJUkhxz2qQ implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Document f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ MessageObject f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ Object f$6;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$MpLQRRhv7itAJQJhQaJUkhxz2qQ(SendMessagesHelper sendMessagesHelper, Document document, long j, MessageObject messageObject, boolean z, int i, Object obj) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = document;
        this.f$2 = j;
        this.f$3 = messageObject;
        this.f$4 = z;
        this.f$5 = i;
        this.f$6 = obj;
    }

    public final void run() {
        this.f$0.lambda$sendSticker$6$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
