package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.TL_photo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$a2su0LhJhPbUXm5fG2WcDQ_Npn4 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_photo f$1;
    private final /* synthetic */ MessageObject f$2;
    private final /* synthetic */ File f$3;
    private final /* synthetic */ DelayedMessage f$4;
    private final /* synthetic */ String f$5;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$a2su0LhJhPbUXm5fG2WcDQ_Npn4(SendMessagesHelper sendMessagesHelper, TL_photo tL_photo, MessageObject messageObject, File file, DelayedMessage delayedMessage, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_photo;
        this.f$2 = messageObject;
        this.f$3 = file;
        this.f$4 = delayedMessage;
        this.f$5 = str;
    }

    public final void run() {
        this.f$0.lambda$null$1$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
