package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class VoIPFragment$$ExternalSyntheticLambda8 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ VoIPFragment f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ VoIPFragment$$ExternalSyntheticLambda8(VoIPFragment voIPFragment, boolean[] zArr) {
        this.f$0 = voIPFragment;
        this.f$1 = zArr;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.lambda$updateViewState$18(this.f$1, dialogInterface);
    }
}
