package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$SEtHU0XDj7zCn-rzJbCQF3a989Y implements Runnable {
    private final /* synthetic */ FileRefController f$0;
    private final /* synthetic */ TL_messages_sendMultiMedia f$1;
    private final /* synthetic */ Object[] f$2;

    public /* synthetic */ -$$Lambda$FileRefController$SEtHU0XDj7zCn-rzJbCQF3a989Y(FileRefController fileRefController, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, Object[] objArr) {
        this.f$0 = fileRefController;
        this.f$1 = tL_messages_sendMultiMedia;
        this.f$2 = objArr;
    }

    public final void run() {
        this.f$0.lambda$sendErrorToObject$24$FileRefController(this.f$1, this.f$2);
    }
}
