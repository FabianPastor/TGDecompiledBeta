package org.telegram.messenger;

import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv-97eY implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ File f$1;
    private final /* synthetic */ MessageObject f$2;
    private final /* synthetic */ DelayedMessage f$3;
    private final /* synthetic */ String f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv-97eY(SendMessagesHelper sendMessagesHelper, File file, MessageObject messageObject, DelayedMessage delayedMessage, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = file;
        this.f$2 = messageObject;
        this.f$3 = delayedMessage;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$2$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
