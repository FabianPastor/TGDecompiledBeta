package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;

final /* synthetic */ class EditTextCaption$$Lambda$1 implements OnShowListener {
    private final EditTextBoldCursor arg$1;

    EditTextCaption$$Lambda$1(EditTextBoldCursor editTextBoldCursor) {
        this.arg$1 = editTextBoldCursor;
    }

    public void onShow(DialogInterface dialogInterface) {
        EditTextCaption.lambda$makeSelectedUrl$1$EditTextCaption(this.arg$1, dialogInterface);
    }
}