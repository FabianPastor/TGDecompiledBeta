package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda21 implements Runnable {
    public final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda21(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void run() {
        AndroidUtilities.showKeyboard(this.f$0);
    }
}
