package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.Components.BiometricPromtHelper.ContinueCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BiometricPromtHelper$OEA0RNRvsc3CizQofe65tz-aKdE implements OnClickListener {
    private final /* synthetic */ ContinueCallback f$0;

    public /* synthetic */ -$$Lambda$BiometricPromtHelper$OEA0RNRvsc3CizQofe65tz-aKdE(ContinueCallback continueCallback) {
        this.f$0 = continueCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.run(false);
    }
}
