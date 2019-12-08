package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraScanActivity$1$XDjDdiQmgHokgIn71NJwz0taulc implements OnDismissListener {
    private final /* synthetic */ CameraScanActivity f$0;

    public /* synthetic */ -$$Lambda$CameraScanActivity$1$XDjDdiQmgHokgIn71NJwz0taulc(CameraScanActivity cameraScanActivity) {
        this.f$0 = cameraScanActivity;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.onFragmentDestroy();
    }
}
