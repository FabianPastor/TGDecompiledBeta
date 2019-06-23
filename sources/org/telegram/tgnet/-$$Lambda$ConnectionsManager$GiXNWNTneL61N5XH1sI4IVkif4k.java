package org.telegram.tgnet;

import org.telegram.messenger.AccountInstance;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$GiXNWNTneL61N5XH1sI4IVkif4k implements Runnable {
    private final /* synthetic */ int f$0;

    public /* synthetic */ -$$Lambda$ConnectionsManager$GiXNWNTneL61N5XH1sI4IVkif4k(int i) {
        this.f$0 = i;
    }

    public final void run() {
        AccountInstance.getInstance(this.f$0).getMessagesController().updateTimerProc();
    }
}
