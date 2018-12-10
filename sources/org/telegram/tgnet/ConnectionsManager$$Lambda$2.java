package org.telegram.tgnet;

import org.telegram.messenger.MessagesController;

final /* synthetic */ class ConnectionsManager$$Lambda$2 implements Runnable {
    private final int arg$1;

    ConnectionsManager$$Lambda$2(int i) {
        this.arg$1 = i;
    }

    public void run() {
        MessagesController.getInstance(this.arg$1).updateTimerProc();
    }
}
