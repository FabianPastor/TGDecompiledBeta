package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;

final /* synthetic */ class SendMessagesHelper$$Lambda$22 implements Runnable {
    private final MediaSendPrepareWorker arg$1;
    private final int arg$2;
    private final SendingMediaInfo arg$3;
    private final boolean arg$4;

    SendMessagesHelper$$Lambda$22(MediaSendPrepareWorker mediaSendPrepareWorker, int i, SendingMediaInfo sendingMediaInfo, boolean z) {
        this.arg$1 = mediaSendPrepareWorker;
        this.arg$2 = i;
        this.arg$3 = sendingMediaInfo;
        this.arg$4 = z;
    }

    public void run() {
        SendMessagesHelper.lambda$null$53$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3, this.arg$4);
    }
}
