package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.tgnet.TLObject;

final /* synthetic */ class ShareAlert$$Lambda$5 implements Runnable {
    private final ShareAlert arg$1;
    private final TLObject arg$2;
    private final Context arg$3;

    ShareAlert$$Lambda$5(ShareAlert shareAlert, TLObject tLObject, Context context) {
        this.arg$1 = shareAlert;
        this.arg$2 = tLObject;
        this.arg$3 = context;
    }

    public void run() {
        this.arg$1.lambda$null$0$ShareAlert(this.arg$2, this.arg$3);
    }
}
