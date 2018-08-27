package org.telegram.messenger;

import java.io.File;
import org.telegram.tgnet.TLRPC.TL_photo;

final /* synthetic */ class SendMessagesHelper$$Lambda$61 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TL_photo arg$2;
    private final MessageObject arg$3;
    private final File arg$4;
    private final DelayedMessage arg$5;
    private final String arg$6;

    SendMessagesHelper$$Lambda$61(SendMessagesHelper sendMessagesHelper, TL_photo tL_photo, MessageObject messageObject, File file, DelayedMessage delayedMessage, String str) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tL_photo;
        this.arg$3 = messageObject;
        this.arg$4 = file;
        this.arg$5 = delayedMessage;
        this.arg$6 = str;
    }

    public void run() {
        this.arg$1.lambda$null$1$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
