package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class BlockingUpdateView$$Lambda$2 implements OnClickListener {
    private final Context arg$1;

    BlockingUpdateView$$Lambda$2(Context context) {
        this.arg$1 = context;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        BlockingUpdateView.lambda$checkApkInstallPermissions$2$BlockingUpdateView(this.arg$1, dialogInterface, i);
    }
}
