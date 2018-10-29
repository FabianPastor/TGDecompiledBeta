package org.telegram.messenger;

import java.io.File;

final /* synthetic */ class SendMessagesHelper$$Lambda$2 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final DelayedMessage arg$2;
    private final File arg$3;
    private final MessageObject arg$4;

    SendMessagesHelper$$Lambda$2(SendMessagesHelper sendMessagesHelper, DelayedMessage delayedMessage, File file, MessageObject messageObject) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = delayedMessage;
        this.arg$3 = file;
        this.arg$4 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$didReceivedNotification$4$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4);
    }
}
