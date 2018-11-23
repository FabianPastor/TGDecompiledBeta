package org.telegram.p005ui;

import org.telegram.p005ui.SetAdminsActivity.SearchAdapter;

/* renamed from: org.telegram.ui.SetAdminsActivity$SearchAdapter$$Lambda$0 */
final /* synthetic */ class SetAdminsActivity$SearchAdapter$$Lambda$0 implements Runnable {
    private final SearchAdapter arg$1;
    private final String arg$2;

    SetAdminsActivity$SearchAdapter$$Lambda$0(SearchAdapter searchAdapter, String str) {
        this.arg$1 = searchAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$processSearch$1$SetAdminsActivity$SearchAdapter(this.arg$2);
    }
}
