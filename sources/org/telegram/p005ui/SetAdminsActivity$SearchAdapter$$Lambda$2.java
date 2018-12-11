package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.SetAdminsActivity.SearchAdapter;

/* renamed from: org.telegram.ui.SetAdminsActivity$SearchAdapter$$Lambda$2 */
final /* synthetic */ class SetAdminsActivity$SearchAdapter$$Lambda$2 implements Runnable {
    private final SearchAdapter arg$1;
    private final String arg$2;
    private final ArrayList arg$3;

    SetAdminsActivity$SearchAdapter$$Lambda$2(SearchAdapter searchAdapter, String str, ArrayList arrayList) {
        this.arg$1 = searchAdapter;
        this.arg$2 = str;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$0$SetAdminsActivity$SearchAdapter(this.arg$2, this.arg$3);
    }
}
