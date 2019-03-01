package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.PhotoViewer.AnonymousClass7;

final /* synthetic */ class PhotoViewer$7$$Lambda$0 implements DialogsActivityDelegate {
    private final AnonymousClass7 arg$1;
    private final ArrayList arg$2;

    PhotoViewer$7$$Lambda$0(AnonymousClass7 anonymousClass7, ArrayList arrayList) {
        this.arg$1 = anonymousClass7;
        this.arg$2 = arrayList;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$0$PhotoViewer$7(this.arg$2, dialogsActivity, arrayList, charSequence, z);
    }
}
