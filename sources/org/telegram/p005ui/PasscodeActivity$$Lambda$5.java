package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.Components.NumberPicker;

/* renamed from: org.telegram.ui.PasscodeActivity$$Lambda$5 */
final /* synthetic */ class PasscodeActivity$$Lambda$5 implements OnClickListener {
    private final PasscodeActivity arg$1;
    private final NumberPicker arg$2;
    private final int arg$3;

    PasscodeActivity$$Lambda$5(PasscodeActivity passcodeActivity, NumberPicker numberPicker, int i) {
        this.arg$1 = passcodeActivity;
        this.arg$2 = numberPicker;
        this.arg$3 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$3$PasscodeActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
