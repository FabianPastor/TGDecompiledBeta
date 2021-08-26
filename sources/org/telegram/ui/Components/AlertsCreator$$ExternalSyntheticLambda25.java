package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda25 implements DialogInterface.OnClickListener {
    public final /* synthetic */ AlertsCreator.BlockDialogCallback f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda25(AlertsCreator.BlockDialogCallback blockDialogCallback, boolean[] zArr) {
        this.f$0 = blockDialogCallback;
        this.f$1 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.run(this.f$1[0], this.f$1[1]);
    }
}
