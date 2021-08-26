package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class CameraScanActivity$1$$ExternalSyntheticLambda0 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ CameraScanActivity f$0;

    public /* synthetic */ CameraScanActivity$1$$ExternalSyntheticLambda0(CameraScanActivity cameraScanActivity) {
        this.f$0 = cameraScanActivity;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.onFragmentDestroy();
    }
}
