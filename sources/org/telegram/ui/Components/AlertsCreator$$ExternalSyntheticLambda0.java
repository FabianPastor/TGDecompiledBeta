package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.tgnet.ConnectionsManager;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda0 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda0(int i, int i2) {
        this.f$0 = i;
        this.f$1 = i2;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.f$0).cancelRequest(this.f$1, true);
    }
}
