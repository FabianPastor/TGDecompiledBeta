package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.p005ui.MediaActivity.C21652;

/* renamed from: org.telegram.ui.MediaActivity$2$$Lambda$2 */
final /* synthetic */ class MediaActivity$2$$Lambda$2 implements DialogsActivityDelegate {
    private final C21652 arg$1;

    MediaActivity$2$$Lambda$2(C21652 c21652) {
        this.arg$1 = c21652;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$2$MediaActivity$2(dialogsActivity, arrayList, charSequence, z);
    }
}
