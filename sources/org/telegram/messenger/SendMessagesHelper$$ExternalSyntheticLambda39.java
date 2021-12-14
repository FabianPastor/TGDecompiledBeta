package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda39 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$1;
    public final /* synthetic */ File f$2;
    public final /* synthetic */ TLRPC$Document f$3;
    public final /* synthetic */ MessageObject f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda39(SendMessagesHelper sendMessagesHelper, SendMessagesHelper.DelayedMessage delayedMessage, File file, TLRPC$Document tLRPC$Document, MessageObject messageObject) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = delayedMessage;
        this.f$2 = file;
        this.f$3 = tLRPC$Document;
        this.f$4 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$3(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
