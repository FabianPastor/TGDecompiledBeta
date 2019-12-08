package org.telegram.ui.Components.voip;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPHelper$DMzandLH_8HcSvLneI5IhtdRHzk implements OnDismissListener {
    private final /* synthetic */ Runnable f$0;

    public /* synthetic */ -$$Lambda$VoIPHelper$DMzandLH_8HcSvLneI5IhtdRHzk(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        VoIPHelper.lambda$permissionDenied$4(this.f$0, dialogInterface);
    }
}
