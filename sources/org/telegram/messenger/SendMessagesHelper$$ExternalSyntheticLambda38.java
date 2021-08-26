package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.SendMessagesHelper;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda38 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$1;
    public final /* synthetic */ File f$2;
    public final /* synthetic */ MessageObject f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda38(SendMessagesHelper sendMessagesHelper, SendMessagesHelper.DelayedMessage delayedMessage, File file, MessageObject messageObject) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = delayedMessage;
        this.f$2 = file;
        this.f$3 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$4(this.f$1, this.f$2, this.f$3);
    }
}
