package org.telegram.ui.Components;

import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class MemberRequestsBottomSheet$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ EditTextBoldCursor f$0;

    public /* synthetic */ MemberRequestsBottomSheet$$ExternalSyntheticLambda0(EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextBoldCursor;
    }

    public final void run() {
        AndroidUtilities.showKeyboard(this.f$0);
    }
}
