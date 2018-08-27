package org.telegram.messenger;

import java.io.File;

final /* synthetic */ class SendMessagesHelper$$Lambda$1 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final File arg$2;
    private final MessageObject arg$3;
    private final DelayedMessage arg$4;
    private final String arg$5;

    SendMessagesHelper$$Lambda$1(SendMessagesHelper sendMessagesHelper, File file, MessageObject messageObject, DelayedMessage delayedMessage, String str) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = file;
        this.arg$3 = messageObject;
        this.arg$4 = delayedMessage;
        this.arg$5 = str;
    }

    public void run() {
        this.arg$1.lambda$didReceivedNotification$2$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
