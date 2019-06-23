package org.telegram.tgnet;

import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$R5V1iXmwj8PWON-tb_jcTaBhzJo implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ TLObject f$1;

    public /* synthetic */ -$$Lambda$ConnectionsManager$R5V1iXmwj8PWON-tb_jcTaBhzJo(int i, TLObject tLObject) {
        this.f$0 = i;
        this.f$1 = tLObject;
    }

    public final void run() {
        AccountInstance.getInstance(this.f$0).getMessagesController().processUpdates((Updates) this.f$1, false);
    }
}
