package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.MediaActivity.C14672;

final /* synthetic */ class MediaActivity$2$$Lambda$2 implements DialogsActivityDelegate {
    private final C14672 arg$1;

    MediaActivity$2$$Lambda$2(C14672 c14672) {
        this.arg$1 = c14672;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$2$MediaActivity$2(dialogsActivity, arrayList, charSequence, z);
    }
}
