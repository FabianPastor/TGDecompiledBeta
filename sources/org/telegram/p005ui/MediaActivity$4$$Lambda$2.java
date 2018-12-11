package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.p005ui.MediaActivity.CLASSNAME;

/* renamed from: org.telegram.ui.MediaActivity$4$$Lambda$2 */
final /* synthetic */ class MediaActivity$4$$Lambda$2 implements DialogsActivityDelegate {
    private final CLASSNAME arg$1;

    MediaActivity$4$$Lambda$2(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$2$MediaActivity$4(dialogsActivity, arrayList, charSequence, z);
    }
}
