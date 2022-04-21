package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class PasscodeActivity$$ExternalSyntheticLambda17 implements DialogInterface.OnClickListener {
    public final /* synthetic */ PasscodeActivity f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ PasscodeActivity$$ExternalSyntheticLambda17(PasscodeActivity passcodeActivity, NumberPicker numberPicker, int i) {
        this.f$0 = passcodeActivity;
        this.f$1 = numberPicker;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m2681lambda$createView$4$orgtelegramuiPasscodeActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
