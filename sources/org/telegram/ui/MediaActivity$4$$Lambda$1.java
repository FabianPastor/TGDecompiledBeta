package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.MediaActivity.AnonymousClass4;

final /* synthetic */ class MediaActivity$4$$Lambda$1 implements DialogsActivityDelegate {
    private final AnonymousClass4 arg$1;

    MediaActivity$4$$Lambda$1(AnonymousClass4 anonymousClass4) {
        this.arg$1 = anonymousClass4;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$1$MediaActivity$4(dialogsActivity, arrayList, charSequence, z);
    }
}
