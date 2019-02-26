package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class WebviewActivity$$Lambda$1 implements Runnable {
    private final WebviewActivity arg$1;
    private final TLObject arg$2;

    WebviewActivity$$Lambda$1(WebviewActivity webviewActivity, TLObject tLObject) {
        this.arg$1 = webviewActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$0$WebviewActivity(this.arg$2);
    }
}
