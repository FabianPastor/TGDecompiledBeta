package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BiometricPromtHelper$TXMp1z6RNX9zEMWYnBxwe83XkOg implements OnClickListener {
    private final /* synthetic */ boolean f$0;
    private final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ -$$Lambda$BiometricPromtHelper$TXMp1z6RNX9zEMWYnBxwe83XkOg(boolean z, BaseFragment baseFragment) {
        this.f$0 = z;
        this.f$1 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        BiometricPromtHelper.lambda$askForBiometric$3(this.f$0, this.f$1, dialogInterface, i);
    }
}
