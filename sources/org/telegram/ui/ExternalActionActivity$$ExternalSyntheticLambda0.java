package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.ConnectionsManager;

public final /* synthetic */ class ExternalActionActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int[] f$1;

    public /* synthetic */ ExternalActionActivity$$ExternalSyntheticLambda0(int i, int[] iArr) {
        this.f$0 = i;
        this.f$1 = iArr;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.f$0).cancelRequest(this.f$1[0], true);
    }
}
