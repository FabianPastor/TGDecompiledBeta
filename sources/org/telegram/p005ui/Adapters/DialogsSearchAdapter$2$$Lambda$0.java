package org.telegram.p005ui.Adapters;

import org.telegram.p005ui.Adapters.DialogsSearchAdapter.CLASSNAME;

/* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$2$$Lambda$0 */
final /* synthetic */ class DialogsSearchAdapter$2$$Lambda$0 implements Runnable {
    private final CLASSNAME arg$1;
    private final String arg$2;

    DialogsSearchAdapter$2$$Lambda$0(CLASSNAME CLASSNAME, String str) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$run$0$DialogsSearchAdapter$2(this.arg$2);
    }
}
