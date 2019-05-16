package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.tgnet.ConnectionsManager;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExternalActionActivity$JSGDZffGA-C8G3znC_C8fmn13dg implements OnCancelListener {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ int[] f$1;

    public /* synthetic */ -$$Lambda$ExternalActionActivity$JSGDZffGA-C8G3znC_C8fmn13dg(int i, int[] iArr) {
        this.f$0 = i;
        this.f$1 = iArr;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.f$0).cancelRequest(this.f$1[0], true);
    }
}
