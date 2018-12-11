package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.Components.EditTextCaption$$Lambda$0 */
final /* synthetic */ class EditTextCaption$$Lambda$0 implements OnClickListener {
    private final EditTextCaption arg$1;
    private final int arg$2;
    private final int arg$3;
    private final EditTextBoldCursor arg$4;

    EditTextCaption$$Lambda$0(EditTextCaption editTextCaption, int i, int i2, EditTextBoldCursor editTextBoldCursor) {
        this.arg$1 = editTextCaption;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = editTextBoldCursor;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$makeSelectedUrl$0$EditTextCaption(this.arg$2, this.arg$3, this.arg$4, dialogInterface, i);
    }
}
