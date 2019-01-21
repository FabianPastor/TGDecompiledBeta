package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

final /* synthetic */ class ShareActivity$$Lambda$0 implements OnDismissListener {
    private final ShareActivity arg$1;

    ShareActivity$$Lambda$0(ShareActivity shareActivity) {
        this.arg$1 = shareActivity;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.arg$1.lambda$onCreate$0$ShareActivity(dialogInterface);
    }
}
