package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.SharedConfig;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda33 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int[] f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda33(int[] iArr) {
        this.f$0 = iArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        SharedConfig.setKeepMedia(this.f$0[0]);
    }
}
