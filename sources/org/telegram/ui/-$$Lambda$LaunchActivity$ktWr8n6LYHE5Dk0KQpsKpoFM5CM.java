package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.tgnet.ConnectionsManager;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$ktWr8n6LYHE5Dk0KQpsKpoFM5CM implements OnCancelListener {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ int[] f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$ktWr8n6LYHE5Dk0KQpsKpoFM5CM(int i, int[] iArr) {
        this.f$0 = i;
        this.f$1 = iArr;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.f$0).cancelRequest(this.f$1[0], true);
    }
}
