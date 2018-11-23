package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.p005ui.MediaActivity.C14712;

/* renamed from: org.telegram.ui.MediaActivity$2$$Lambda$2 */
final /* synthetic */ class MediaActivity$2$$Lambda$2 implements DialogsActivityDelegate {
    private final C14712 arg$1;

    MediaActivity$2$$Lambda$2(C14712 c14712) {
        this.arg$1 = c14712;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$2$MediaActivity$2(dialogsActivity, arrayList, charSequence, z);
    }
}
