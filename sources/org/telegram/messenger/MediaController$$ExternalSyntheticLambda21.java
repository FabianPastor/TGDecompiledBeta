package org.telegram.messenger;

import android.content.DialogInterface;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda21 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ boolean[] f$0;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda21(boolean[] zArr) {
        this.f$0 = zArr;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        MediaController.lambda$saveFile$33(this.f$0, dialogInterface);
    }
}
