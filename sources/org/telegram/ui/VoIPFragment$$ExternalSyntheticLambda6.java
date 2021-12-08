package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class VoIPFragment$$ExternalSyntheticLambda6 implements DialogInterface.OnClickListener {
    public final /* synthetic */ VoIPFragment f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ VoIPFragment$$ExternalSyntheticLambda6(VoIPFragment voIPFragment, boolean[] zArr) {
        this.f$0 = voIPFragment;
        this.f$1 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$updateViewState$17(this.f$1, dialogInterface, i);
    }
}
