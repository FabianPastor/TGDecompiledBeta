package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.SendMessagesHelper;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda16(SendMessagesHelper sendMessagesHelper, File file, MessageObject messageObject, SendMessagesHelper.DelayedMessage delayedMessage, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = file;
        this.f$2 = messageObject;
        this.f$3 = delayedMessage;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.m421x5b683917(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
