package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$yo_APENFhJaMvPnFq6-LptqBCFU implements OnCancelListener {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ int[] f$1;
    private final /* synthetic */ Runnable f$2;

    public /* synthetic */ -$$Lambda$LaunchActivity$yo_APENFhJaMvPnFq6-LptqBCFU(int i, int[] iArr, Runnable runnable) {
        this.f$0 = i;
        this.f$1 = iArr;
        this.f$2 = runnable;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        LaunchActivity.lambda$runLinkRequest$40(this.f$0, this.f$1, this.f$2, dialogInterface);
    }
}
