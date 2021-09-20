package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;

public final /* synthetic */ class BlockingUpdateView$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ BlockingUpdateView$$ExternalSyntheticLambda0(Context context) {
        this.f$0 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        BlockingUpdateView.lambda$checkApkInstallPermissions$2(this.f$0, dialogInterface, i);
    }
}
