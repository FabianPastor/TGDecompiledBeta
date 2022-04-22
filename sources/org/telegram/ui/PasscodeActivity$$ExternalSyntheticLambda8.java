package org.telegram.ui;

import android.view.View;

public final /* synthetic */ class PasscodeActivity$$ExternalSyntheticLambda8 implements View.OnFocusChangeListener {
    public final /* synthetic */ PasscodeActivity f$0;
    public final /* synthetic */ CodeNumberField f$1;

    public /* synthetic */ PasscodeActivity$$ExternalSyntheticLambda8(PasscodeActivity passcodeActivity, CodeNumberField codeNumberField) {
        this.f$0 = passcodeActivity;
        this.f$1 = codeNumberField;
    }

    public final void onFocusChange(View view, boolean z) {
        this.f$0.lambda$createView$11(this.f$1, view, z);
    }
}
