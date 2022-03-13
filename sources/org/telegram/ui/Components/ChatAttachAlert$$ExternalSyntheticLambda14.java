package org.telegram.ui.Components;

import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda14(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void run() {
        AndroidUtilities.showKeyboard(this.f$0);
    }
}
