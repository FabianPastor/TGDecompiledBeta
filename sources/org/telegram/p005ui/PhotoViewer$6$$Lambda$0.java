package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.p005ui.PhotoViewer.C15396;

/* renamed from: org.telegram.ui.PhotoViewer$6$$Lambda$0 */
final /* synthetic */ class PhotoViewer$6$$Lambda$0 implements DialogsActivityDelegate {
    private final C15396 arg$1;
    private final ArrayList arg$2;

    PhotoViewer$6$$Lambda$0(C15396 c15396, ArrayList arrayList) {
        this.arg$1 = c15396;
        this.arg$2 = arrayList;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$0$PhotoViewer$6(this.arg$2, dialogsActivity, arrayList, charSequence, z);
    }
}
