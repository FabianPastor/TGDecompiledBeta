package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda1 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda1(int i, int[] iArr, Runnable runnable) {
        this.f$0 = i;
        this.f$1 = iArr;
        this.f$2 = runnable;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        LaunchActivity.lambda$runImportRequest$29(this.f$0, this.f$1, this.f$2, dialogInterface);
    }
}
