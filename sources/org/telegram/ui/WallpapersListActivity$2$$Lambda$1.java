package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.WallpapersListActivity.AnonymousClass2;

final /* synthetic */ class WallpapersListActivity$2$$Lambda$1 implements DialogsActivityDelegate {
    private final AnonymousClass2 arg$1;

    WallpapersListActivity$2$$Lambda$1(AnonymousClass2 anonymousClass2) {
        this.arg$1 = anonymousClass2;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onItemClick$3$WallpapersListActivity$2(dialogsActivity, arrayList, charSequence, z);
    }
}
