package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$0g5iS90DmKAoHgZX7FJyyhE5gX4 implements Runnable {
    private final /* synthetic */ MediaSendPrepareWorker f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ SendingMediaInfo f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$0g5iS90DmKAoHgZX7FJyyhE5gX4(MediaSendPrepareWorker mediaSendPrepareWorker, int i, SendingMediaInfo sendingMediaInfo, boolean z) {
        this.f$0 = mediaSendPrepareWorker;
        this.f$1 = i;
        this.f$2 = sendingMediaInfo;
        this.f$3 = z;
    }

    public final void run() {
        SendMessagesHelper.lambda$null$54(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
