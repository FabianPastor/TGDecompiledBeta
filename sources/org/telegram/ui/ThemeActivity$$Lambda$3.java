package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

final /* synthetic */ class ThemeActivity$$Lambda$3 implements OnShowListener {
    private final EditTextBoldCursor arg$1;

    ThemeActivity$$Lambda$3(EditTextBoldCursor editTextBoldCursor) {
        this.arg$1 = editTextBoldCursor;
    }

    public void onShow(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new ThemeActivity$$Lambda$9(this.arg$1));
    }
}
