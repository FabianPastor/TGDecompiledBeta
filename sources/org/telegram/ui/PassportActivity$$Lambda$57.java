package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator.DatePickerDelegate;
import org.telegram.ui.Components.EditTextBoldCursor;

final /* synthetic */ class PassportActivity$$Lambda$57 implements DatePickerDelegate {
    private final PassportActivity arg$1;
    private final int arg$2;
    private final EditTextBoldCursor arg$3;

    PassportActivity$$Lambda$57(PassportActivity passportActivity, int i, EditTextBoldCursor editTextBoldCursor) {
        this.arg$1 = passportActivity;
        this.arg$2 = i;
        this.arg$3 = editTextBoldCursor;
    }

    public void didSelectDate(int i, int i2, int i3) {
        this.arg$1.lambda$null$47$PassportActivity(this.arg$2, this.arg$3, i, i2, i3);
    }
}
