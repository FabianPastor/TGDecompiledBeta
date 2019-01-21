package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

final /* synthetic */ class PasscodeView$$Lambda$5 implements OnDismissListener {
    private final PasscodeView arg$1;

    PasscodeView$$Lambda$5(PasscodeView passcodeView) {
        this.arg$1 = passcodeView;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$checkFingerprint$5$PasscodeView(dialogInterface);
    }
}
