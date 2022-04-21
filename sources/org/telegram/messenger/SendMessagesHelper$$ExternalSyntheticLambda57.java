package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.TL_photo f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ File f$3;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$4;
    public final /* synthetic */ String f$5;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda57(SendMessagesHelper sendMessagesHelper, TLRPC.TL_photo tL_photo, MessageObject messageObject, File file, SendMessagesHelper.DelayedMessage delayedMessage, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_photo;
        this.f$2 = messageObject;
        this.f$3 = file;
        this.f$4 = delayedMessage;
        this.f$5 = str;
    }

    public final void run() {
        this.f$0.m431x7626ca56(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
