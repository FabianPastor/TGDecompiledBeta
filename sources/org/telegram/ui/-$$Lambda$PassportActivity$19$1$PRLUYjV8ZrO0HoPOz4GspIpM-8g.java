package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$19$1$PRLUYjV8ZrO0HoPOz4GspIpM-8g implements Runnable {
    private final /* synthetic */ ErrorRunnable f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$PassportActivity$19$1$PRLUYjV8ZrO0HoPOz4GspIpM-8g(ErrorRunnable errorRunnable, TL_error tL_error, String str) {
        this.f$0 = errorRunnable;
        this.f$1 = tL_error;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.onError(this.f$1.text, this.f$2);
    }
}