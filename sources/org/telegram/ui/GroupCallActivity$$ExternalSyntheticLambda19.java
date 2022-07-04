package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.EditTextBoldCursor;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda19 implements Runnable {
    public final /* synthetic */ BottomSheet f$0;
    public final /* synthetic */ EditTextBoldCursor f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ AlertDialog f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda19(BottomSheet bottomSheet, EditTextBoldCursor editTextBoldCursor, boolean z, AlertDialog alertDialog) {
        this.f$0 = bottomSheet;
        this.f$1 = editTextBoldCursor;
        this.f$2 = z;
        this.f$3 = alertDialog;
    }

    public final void run() {
        GroupCallActivity.lambda$makeFocusable$7(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
