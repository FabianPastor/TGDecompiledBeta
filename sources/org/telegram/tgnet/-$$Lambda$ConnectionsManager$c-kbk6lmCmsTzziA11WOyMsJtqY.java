package org.telegram.tgnet;

import org.telegram.messenger.MessagesController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$c-kbk6lmCmsTzziA11WOyMsJtqY implements Runnable {
    private final /* synthetic */ int f$0;

    public /* synthetic */ -$$Lambda$ConnectionsManager$c-kbk6lmCmsTzziA11WOyMsJtqY(int i) {
        this.f$0 = i;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).getDifference();
    }
}
