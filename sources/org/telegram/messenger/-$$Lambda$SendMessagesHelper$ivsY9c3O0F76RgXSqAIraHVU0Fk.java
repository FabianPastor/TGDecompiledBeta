package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$ivsY9c3O0var_RgXSqAIraHVU0Fk implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ DelayedMessage f$1;
    private final /* synthetic */ File f$2;
    private final /* synthetic */ Document f$3;
    private final /* synthetic */ MessageObject f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$ivsY9c3O0var_RgXSqAIraHVU0Fk(SendMessagesHelper sendMessagesHelper, DelayedMessage delayedMessage, File file, Document document, MessageObject messageObject) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = delayedMessage;
        this.f$2 = file;
        this.f$3 = document;
        this.f$4 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$null$3$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
