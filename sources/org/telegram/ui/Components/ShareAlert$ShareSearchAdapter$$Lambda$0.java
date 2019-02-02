package org.telegram.ui.Components;

import org.telegram.ui.Components.ShareAlert.ShareSearchAdapter;

final /* synthetic */ class ShareAlert$ShareSearchAdapter$$Lambda$0 implements Runnable {
    private final ShareSearchAdapter arg$1;
    private final String arg$2;
    private final int arg$3;

    ShareAlert$ShareSearchAdapter$$Lambda$0(ShareSearchAdapter shareSearchAdapter, String str, int i) {
        this.arg$1 = shareSearchAdapter;
        this.arg$2 = str;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(this.arg$2, this.arg$3);
    }
}
