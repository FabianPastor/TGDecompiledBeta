package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PassportActivity$19$1$$Lambda$2 implements Runnable {
    private final ErrorRunnable arg$1;
    private final TL_error arg$2;
    private final String arg$3;

    PassportActivity$19$1$$Lambda$2(ErrorRunnable errorRunnable, TL_error tL_error, String str) {
        this.arg$1 = errorRunnable;
        this.arg$2 = tL_error;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.onError(this.arg$2.text, this.arg$3);
    }
}
