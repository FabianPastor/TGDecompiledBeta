package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.MediaActivity.C16252;

final /* synthetic */ class MediaActivity$2$$Lambda$2 implements DialogsActivityDelegate {
    private final C16252 arg$1;

    MediaActivity$2$$Lambda$2(C16252 c16252) {
        this.arg$1 = c16252;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$2$MediaActivity$2(dialogsActivity, arrayList, charSequence, z);
    }
}
