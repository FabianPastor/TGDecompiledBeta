package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.tgnet.ConnectionsManager;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$nZ0vhjin-DIdWeCiq6nwyI2M8P0 implements OnCancelListener {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$nZ0vhjin-DIdWeCiq6nwyI2M8P0(int i, int i2) {
        this.f$0 = i;
        this.f$1 = i2;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.f$0).cancelRequest(this.f$1, true);
    }
}
