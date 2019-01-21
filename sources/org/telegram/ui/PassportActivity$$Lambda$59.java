package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.Components.EditTextBoldCursor;

final /* synthetic */ class PassportActivity$$Lambda$59 implements OnClickListener {
    private final PassportActivity arg$1;
    private final EditTextBoldCursor arg$2;

    PassportActivity$$Lambda$59(PassportActivity passportActivity, EditTextBoldCursor editTextBoldCursor) {
        this.arg$1 = passportActivity;
        this.arg$2 = editTextBoldCursor;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$49$PassportActivity(this.arg$2, dialogInterface, i);
    }
}
