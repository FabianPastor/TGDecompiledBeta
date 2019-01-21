package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

final /* synthetic */ class ThemeActivity$$Lambda$7 implements OnShowListener {
    private final EditTextBoldCursor arg$1;

    ThemeActivity$$Lambda$7(EditTextBoldCursor editTextBoldCursor) {
        this.arg$1 = editTextBoldCursor;
    }

    public void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new ThemeActivity$$Lambda$10(this.arg$1));
    }
}
