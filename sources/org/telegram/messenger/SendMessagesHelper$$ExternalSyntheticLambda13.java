package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ SendMessagesHelper.MediaSendPrepareWorker f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ SendMessagesHelper.SendingMediaInfo f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda13(SendMessagesHelper.MediaSendPrepareWorker mediaSendPrepareWorker, AccountInstance accountInstance, SendMessagesHelper.SendingMediaInfo sendingMediaInfo, boolean z) {
        this.f$0 = mediaSendPrepareWorker;
        this.f$1 = accountInstance;
        this.f$2 = sendingMediaInfo;
        this.f$3 = z;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareSendingMedia$84(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
